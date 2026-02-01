package com.example.birthdays.services;

import com.example.birthdays.entities.Person;
import com.example.birthdays.repositories.PersonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

@Component
public class BirthdayConsoleApp implements CommandLineRunner {

    private final PersonRepository repository;
    private final Scanner scanner = new Scanner(System.in);

    public BirthdayConsoleApp(PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        System.out.println("=== Приложение 'Дни Рождения' запущено ===");
        showupcomingBirthdaysEmpty();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> showAll();
                case "2" -> addPerson();
                case "3" -> showupcomingBirthdaysEmpty();
                case "4" -> editPerson();
                case "5" -> deletePerson();
                case "0" -> running = false;
                default -> System.out.println("Неверный ввод.");
            }
        }
    }

    private void showupcomingBirthdaysEmpty() {
        LocalDate today = LocalDate.now();
        List<Person> todayList = repository.findByMonthAndDay(today.getMonthValue(), today.getDayOfMonth());
        short upcomingBirthdaysEmpty = 0;

        System.out.println("\n--- Именинники ближайшей недели: ---");

        for (int i = 0; i <= 7; i++) {
            LocalDate targetDate = today.plusDays(i);
            List<Person> birthdays = repository.findByMonthAndDay(
                    targetDate.getMonthValue(),
                    targetDate.getDayOfMonth()
            );

            if (!birthdays.isEmpty()) {
                String dateInfo = (i == 0) ? "Сегодня" : targetDate.toString();
                birthdays.forEach(p -> System.out.println(dateInfo + ": " + p.getName()));
                upcomingBirthdaysEmpty--;
            }
        }

        if (upcomingBirthdaysEmpty == 0) System.out.println("Праздников не намечается.");
    }

    private void addPerson() {
        System.out.print("Введите имя: ");
        String name = scanner.nextLine();
        System.out.print("Введите дату (ГГГГ-ММ-ДД): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());

        repository.save(new Person(name, date));
        System.out.println("Сохранено!");
    }

    private void editPerson() {
        System.out.print("Введите ID записи для редактирования: ");
        Long id = Long.parseLong(scanner.nextLine());

        // 1. Ищем запись в БД
        repository.findById(id).ifPresentOrElse(person -> {
            System.out.println("Найдено: " + person.getName() + " (" + person.getBirthday() + ")");

            System.out.print("Введите новое имя (оставьте пустым, чтобы не менять): ");
            String newName = scanner.nextLine();
            if (!newName.isBlank()) person.setName(newName);

            System.out.print("Введите новую дату ГГГГ-ММ-ДД (оставьте пустым, чтобы не менять): ");
            String newDate = scanner.nextLine();
            if (!newDate.isBlank()) person.setBirthday(LocalDate.parse(newDate));

            // 2. Сохраняем обновленный объект
            repository.save(person);
            System.out.println("Запись обновлена!");
        }, () -> System.out.println("Запись с таким ID не найдена."));
    }

    private void deletePerson() {
        System.out.print("Введите ID записи для удаления: ");
        Long id = Long.parseLong(scanner.nextLine());

        if (repository.existsById(id)) {
            repository.deleteById(id);
            System.out.println("Запись успешно удалена.");
        } else {
            System.out.println("Запись не найдена.");
        }
    }

    private void showAll() {
        repository.findAll().forEach(System.out::println);
    }

    private void printMenu() {
        System.out.println("\nМеню: \n1. Список всех \n2. Добавить \n3. Ближайшие ДР \n4. Изменить запись \n5. Удалить запись \n0. Выход");
    }

}
