# Java Programming Exam Auto Correction [![Maven Central](https://img.shields.io/maven-central/v/io.github.manoelcampos/programming-exam-auto-correction.svg?label=Maven%20Central)](https://central.sonatype.com/search?q=programming-exam-auto-correction&namespace=io.github.manoelcampos) 

An experimental library to enable creating automatated tests for correcting Java programming exams using [ArchUnit](http://archunit.org).

The library is designed to be used in combination with [JUnit 5](https://junit.org/junit5/), 
providing a set of abstract test classes you can use to create your own tests.
Those theses are intended to check the correctness of the code written by the students.
But mainly, the library enables you to check if the structure of the students' code is 
as expected. For instance, you can check if the students have created the expected classes,
if they have the expected methods and attributes, if the attribute types are correct, etc.

For more details on how to use these abstract classes to write your own JUnit 5 tests,
check this [sample repository](https://github.com/manoelcampos/fp-exercicio-imutabilidade-java).
