package ma.lucidea.abcidea.service.impl;

import ma.lucidea.abcidea.service.SchoolService;
import ma.lucidea.abcidea.domain.School;
import ma.lucidea.abcidea.repository.SchoolRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service Implementation for managing School.
 */
@Service
@Transactional
public class SchoolServiceImpl implements SchoolService {

    private final Logger log = LoggerFactory.getLogger(SchoolServiceImpl.class);

    private final SchoolRepository schoolRepository;

    public SchoolServiceImpl(SchoolRepository schoolRepository) {
        this.schoolRepository = schoolRepository;
    }

    /**
     * Save a school.
     *
     * @param school the entity to save
     * @return the persisted entity
     */
    @Override
    public School save(School school) {
        log.debug("Request to save School : {}", school);
        return schoolRepository.save(school);
    }

    /**
     * Get all the schools.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<School> findAll() {
        log.debug("Request to get all Schools");
        return schoolRepository.findAll();
    }


    /**
     * Get one school by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<School> findOne(Long id) {
        log.debug("Request to get School : {}", id);
        return schoolRepository.findById(id);
    }

    /**
     * Delete the school by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete School : {}", id);
        schoolRepository.deleteById(id);
    }
}
