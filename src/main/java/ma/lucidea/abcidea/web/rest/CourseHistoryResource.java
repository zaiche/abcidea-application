package ma.lucidea.abcidea.web.rest;
import ma.lucidea.abcidea.domain.CourseHistory;
import ma.lucidea.abcidea.service.CourseHistoryService;
import ma.lucidea.abcidea.web.rest.errors.BadRequestAlertException;
import ma.lucidea.abcidea.web.rest.util.HeaderUtil;
import ma.lucidea.abcidea.web.rest.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing CourseHistory.
 */
@RestController
@RequestMapping("/api")
public class CourseHistoryResource {

    private final Logger log = LoggerFactory.getLogger(CourseHistoryResource.class);

    private static final String ENTITY_NAME = "abcideaAppCourseHistory";

    private final CourseHistoryService courseHistoryService;

    public CourseHistoryResource(CourseHistoryService courseHistoryService) {
        this.courseHistoryService = courseHistoryService;
    }

    /**
     * POST  /course-histories : Create a new courseHistory.
     *
     * @param courseHistory the courseHistory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new courseHistory, or with status 400 (Bad Request) if the courseHistory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/course-histories")
    public ResponseEntity<CourseHistory> createCourseHistory(@RequestBody CourseHistory courseHistory) throws URISyntaxException {
        log.debug("REST request to save CourseHistory : {}", courseHistory);
        if (courseHistory.getId() != null) {
            throw new BadRequestAlertException("A new courseHistory cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CourseHistory result = courseHistoryService.save(courseHistory);
        return ResponseEntity.created(new URI("/api/course-histories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /course-histories : Updates an existing courseHistory.
     *
     * @param courseHistory the courseHistory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated courseHistory,
     * or with status 400 (Bad Request) if the courseHistory is not valid,
     * or with status 500 (Internal Server Error) if the courseHistory couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/course-histories")
    public ResponseEntity<CourseHistory> updateCourseHistory(@RequestBody CourseHistory courseHistory) throws URISyntaxException {
        log.debug("REST request to update CourseHistory : {}", courseHistory);
        if (courseHistory.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CourseHistory result = courseHistoryService.save(courseHistory);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, courseHistory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /course-histories : get all the courseHistories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of courseHistories in body
     */
    @GetMapping("/course-histories")
    public ResponseEntity<List<CourseHistory>> getAllCourseHistories(Pageable pageable) {
        log.debug("REST request to get a page of CourseHistories");
        Page<CourseHistory> page = courseHistoryService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/course-histories");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET  /course-histories/:id : get the "id" courseHistory.
     *
     * @param id the id of the courseHistory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the courseHistory, or with status 404 (Not Found)
     */
    @GetMapping("/course-histories/{id}")
    public ResponseEntity<CourseHistory> getCourseHistory(@PathVariable Long id) {
        log.debug("REST request to get CourseHistory : {}", id);
        Optional<CourseHistory> courseHistory = courseHistoryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(courseHistory);
    }

    /**
     * DELETE  /course-histories/:id : delete the "id" courseHistory.
     *
     * @param id the id of the courseHistory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/course-histories/{id}")
    public ResponseEntity<Void> deleteCourseHistory(@PathVariable Long id) {
        log.debug("REST request to delete CourseHistory : {}", id);
        courseHistoryService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
