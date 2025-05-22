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

    public static User getUserWithoutEmail() {
        return User.builder()
                .password(faker.internet().password(8, 16))
                .name(faker.name().fullName())
                .build();
    }
}