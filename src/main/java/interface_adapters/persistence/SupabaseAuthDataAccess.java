package interface_adapters.persistence;

import app_business_rules.login.LoginDataAccessInterface;
import app_business_rules.signup.SignUpDataAccessInterface;
import entities.users.User;
import superbase.SupabaseUserDao;

import java.sql.SQLException;
import java.util.Collections;
import java.util.UUID;

/**
 * Adapts the Supabase user DAO to authentication use cases.
 */
public class SupabaseAuthDataAccess
        implements SignUpDataAccessInterface, LoginDataAccessInterface {

    private final SupabaseUserDao userDao;

    public SupabaseAuthDataAccess(SupabaseUserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public boolean existsByUsername(String username) {
        try {
            return userDao.existsByUsername(username);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to check username availability.", exception);
        }
    }

    @Override
    public UUID saveUser(User user) {
        try {
            return userDao.save(
                    user.getUsername(),
                    user.getPasswordHash(),
                    user.getFirstName(),
                    user.getLastName());
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to save user.", exception);
        }
    }

    @Override
    public User getUserByUsername(String username) {
        try {
            return userDao.findByUsername(username)
                    .map(row -> UserMapper.toUser(
                            row.id(),
                            row.username(),
                            row.passwordHash(),
                            row.firstName(),
                            row.lastName(),
                            Collections.emptyList()))
                    .orElse(null);
        } catch (SQLException exception) {
            throw new IllegalStateException(
                    "Failed to load user.", exception);
        }
    }
}
