package com.agriflow.marketplace.services;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface générique pour les opérations CRUD (Create, Read, Update, Delete).
 *
 * @param <T> Le type d'entité géré par le service
 */
public interface IService<T> {

    /**
     * Ajoute une nouvelle entité dans la base de données.
     *
     * @param entity L'entité à ajouter
     * @throws SQLException en cas d'erreur SQL
     */
    void ajouter(T entity) throws SQLException;

    /**
     * Modifie une entité existante dans la base de données.
     *
     * @param entity L'entité à modifier
     * @throws SQLException en cas d'erreur SQL
     */
    void modifier(T entity) throws SQLException;

    /**
     * Supprime une entité de la base de données.
     *
     * @param entity L'entité à supprimer
     * @throws SQLException en cas d'erreur SQL
     */
    void supprimer(T entity) throws SQLException;

    /**
     * Récupère toutes les entités de la base de données.
     *
     * @return Liste de toutes les entités
     * @throws SQLException en cas d'erreur SQL
     */
    List<T> recuperer() throws SQLException;

    /**
     * Récupère une entité par son identifiant.
     *
     * @param id L'identifiant de l'entité
     * @return L'entité trouvée, ou null si elle n'existe pas
     * @throws SQLException en cas d'erreur SQL
     */
    T recupererParId(int id) throws SQLException;
}
