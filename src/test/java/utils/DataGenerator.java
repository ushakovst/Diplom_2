package utils;

import net.datafaker.Faker;
import models.User;

public class DataGenerator {
    private static final Faker faker = new Faker();

    public static User generateUser() {
        return User.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password(8, 16))
                .name(faker.name().fullName())
                .build();
    }

    public static User generateUserWithoutEmail() {
        return User.builder()
                .email("")
                .password(faker.internet().password(8, 16))
                .name(faker.name().fullName())
                .build();
    }

    public static User generateUserWithoutPassword() {
        return User.builder()
                .email(faker.internet().emailAddress())
                .password("")
                .name(faker.name().fullName())
                .build();
    }

    public static User generateUserWithoutName() {
        return User.builder()
                .email(faker.internet().emailAddress())
                .password(faker.internet().password(8, 16))
                .name("")
                .build();
    }
}