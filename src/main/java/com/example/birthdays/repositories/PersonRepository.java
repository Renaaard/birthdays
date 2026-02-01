package com.example.birthdays.repositories;

import com.example.birthdays.entities.Person;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    @Query("SELECT p FROM Person p WHERE MONTH(p.birthday) = :month AND DAY(p.birthday) = :day")
    List<Person> findByMonthAndDay(@Param("month") int month, @Param("day") int day);
}
