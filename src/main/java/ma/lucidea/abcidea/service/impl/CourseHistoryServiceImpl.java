package ma.lucidea.abcidea.service.impl;

import ma.lucidea.abcidea.service.CourseHistoryService;
import ma.lucidea.abcidea.domain.CourseHistory;
import ma.lucidea.abcidea.repository.CourseHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service Implementation for managing CourseHistory.
 */
@Service
@Transactional
public class CourseHistoryServiceImpl implements CourseHistoryService {

    private final Logger log = LoggerFactory.getLogger(CourseHistoryServiceImpl.class);

    private final CourseHistoryRepository courseHistoryRepository;

    public CourseHistoryServiceImpl(CourseHistoryRepository courseHistoryRepository) {
        this.courseHistoryRepository = courseHistoryRepository;
    }

    /**
     * Save a courseHistory.
     *
     * @param courseHistory the entity to save
     * @return the persisted entity
     */
    @Override
    public CourseHistory save(CourseHistory courseHistory) {
        log.debug("Request to save CourseHistory : {}", courseHistory);
        return courseHistoryRepository.save(courseHistory);
    }

    /**
     * Get all the courseHistories.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CourseHistory> findAll(Pageable pageable) {
        log.debug("Request to get all CourseHistories");
        return courseHistoryRepository.findAll(pageable);
    }


    /**
     * Get one courseHistory by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<CourseHistory> findOne(Long id) {
        log.debug("Request to get CourseHistory : {}", id);
        return courseHistoryRepository.findById(id);
    }

    /**
     * Delete the courseHistory by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete CourseHistory : {}", id);
        courseHistoryRepository.deleteById(id);
    }
}
