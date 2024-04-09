package cz.phishingalert.scraper.repository

interface CrudRepository<ENTITY, ID> {
    /**
     * Create the row from given entity
     */
    fun create(entity: ENTITY): ENTITY

    /**
     * Find the row with the given id and transform it to the correct ENTITY type
     */
    fun find(id: ID): ENTITY?

    /**
     * Get all rows from the repository
     */
    fun findAll(): Collection<ENTITY>

    /**
     * Delete the row with given id
     */
    fun delete(id: ID): Boolean
}