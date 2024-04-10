package cz.phishingalert.scraper.repository.generic

/**
 * Interface for repository performing basic database operations
 */
interface CrudRepository<MODEL, ID> {
    /**
     * Create the row from given model
     * @return the created model, null if it couldn't be created
     */
    fun create(entity: MODEL): MODEL

    /**
     * Find the row with the given id and transform it to the correct ENTITY type
     * @return the found model, null if it wasn't found
     */
    fun find(id: ID): MODEL?

    /**
     * Get all rows from the repository
     * @return a collection of all the rows converted to model
     */
    fun findAll(): Collection<MODEL>

    /**
     * Delete the row with given id
     * @return true if the row was found and deleted, false otherwise
     */
    fun delete(id: ID): Boolean
}