package org.example.novyru.Infrastructure.Repositories;

import org.example.novyru.Domain.Interfaces.Repositories.IUserRepository;
import org.example.novyru.Domain.Models.User;
import org.example.novyru.Infrastructure.Entities.UserEntity;
import org.example.novyru.Infrastructure.Mappers.UserMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserMapper userMapper;
    private final TransactionTemplate transactionTemplate;

    public UserRepository(
            UserMapper userMapper,
            PlatformTransactionManager transactionManager
    ) {
        this.userMapper = userMapper;
        this.transactionTemplate =
                new TransactionTemplate(transactionManager);
    }

    // FIND USER BY EMAIL
    @Override
    public Optional<User> findByEmail(String email) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserEntity> query =
                cb.createQuery(UserEntity.class);

        Root<UserEntity> user = query.from(UserEntity.class);

        query.select(user)
                .where(cb.equal(user.get("email"), email));

        return entityManager.createQuery(query)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .map(userMapper::toModel);
    }

    // SAVE USER
    @Override
    public boolean save(User user) {

        UserEntity entity = userMapper.toEntity(user);

        try {
            transactionTemplate.executeWithoutResult(status -> {
                entityManager.persist(entity);
                entityManager.flush();
            });

            return true;

        } catch (DataIntegrityViolationException e) {

            for (Throwable cause = e;
                 cause != null;
                 cause = cause.getCause()) {

                if (cause instanceof SQLException sqlException
                        && sqlException.getErrorCode() == 1062) {
                    return false;
                }
            }

            throw e;
        }
    }
}