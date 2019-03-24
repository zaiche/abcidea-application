package ma.lucidea.abcidea.web.rest;

import ma.lucidea.abcidea.AbcideaApp;

import ma.lucidea.abcidea.domain.CourseHistory;
import ma.lucidea.abcidea.repository.CourseHistoryRepository;
import ma.lucidea.abcidea.service.CourseHistoryService;
import ma.lucidea.abcidea.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;


import static ma.lucidea.abcidea.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ma.lucidea.abcidea.domain.enumeration.Language;
/**
 * Test class for the CourseHistoryResource REST controller.
 *
 * @see CourseHistoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = AbcideaApp.class)
public class CourseHistoryResourceIntTest {

    private static final Instant DEFAULT_START_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_START_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_END_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_END_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Language DEFAULT_LANGUAGE = Language.FRENCH;
    private static final Language UPDATED_LANGUAGE = Language.ENGLISH;

    @Autowired
    private CourseHistoryRepository courseHistoryRepository;

    @Autowired
    private CourseHistoryService courseHistoryService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restCourseHistoryMockMvc;

    private CourseHistory courseHistory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CourseHistoryResource courseHistoryResource = new CourseHistoryResource(courseHistoryService);
        this.restCourseHistoryMockMvc = MockMvcBuilders.standaloneSetup(courseHistoryResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CourseHistory createEntity(EntityManager em) {
        CourseHistory courseHistory = new CourseHistory()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .language(DEFAULT_LANGUAGE);
        return courseHistory;
    }

    @Before
    public void initTest() {
        courseHistory = createEntity(em);
    }

    @Test
    @Transactional
    public void createCourseHistory() throws Exception {
        int databaseSizeBeforeCreate = courseHistoryRepository.findAll().size();

        // Create the CourseHistory
        restCourseHistoryMockMvc.perform(post("/api/course-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(courseHistory)))
            .andExpect(status().isCreated());

        // Validate the CourseHistory in the database
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeCreate + 1);
        CourseHistory testCourseHistory = courseHistoryList.get(courseHistoryList.size() - 1);
        assertThat(testCourseHistory.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testCourseHistory.getEndDate()).isEqualTo(DEFAULT_END_DATE);
        assertThat(testCourseHistory.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
    }

    @Test
    @Transactional
    public void createCourseHistoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = courseHistoryRepository.findAll().size();

        // Create the CourseHistory with an existing ID
        courseHistory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCourseHistoryMockMvc.perform(post("/api/course-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(courseHistory)))
            .andExpect(status().isBadRequest());

        // Validate the CourseHistory in the database
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllCourseHistories() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get all the courseHistoryList
        restCourseHistoryMockMvc.perform(get("/api/course-histories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(courseHistory.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())));
    }
    
    @Test
    @Transactional
    public void getCourseHistory() throws Exception {
        // Initialize the database
        courseHistoryRepository.saveAndFlush(courseHistory);

        // Get the courseHistory
        restCourseHistoryMockMvc.perform(get("/api/course-histories/{id}", courseHistory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(courseHistory.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCourseHistory() throws Exception {
        // Get the courseHistory
        restCourseHistoryMockMvc.perform(get("/api/course-histories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCourseHistory() throws Exception {
        // Initialize the database
        courseHistoryService.save(courseHistory);

        int databaseSizeBeforeUpdate = courseHistoryRepository.findAll().size();

        // Update the courseHistory
        CourseHistory updatedCourseHistory = courseHistoryRepository.findById(courseHistory.getId()).get();
        // Disconnect from session so that the updates on updatedCourseHistory are not directly saved in db
        em.detach(updatedCourseHistory);
        updatedCourseHistory
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .language(UPDATED_LANGUAGE);

        restCourseHistoryMockMvc.perform(put("/api/course-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCourseHistory)))
            .andExpect(status().isOk());

        // Validate the CourseHistory in the database
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeUpdate);
        CourseHistory testCourseHistory = courseHistoryList.get(courseHistoryList.size() - 1);
        assertThat(testCourseHistory.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testCourseHistory.getEndDate()).isEqualTo(UPDATED_END_DATE);
        assertThat(testCourseHistory.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
    }

    @Test
    @Transactional
    public void updateNonExistingCourseHistory() throws Exception {
        int databaseSizeBeforeUpdate = courseHistoryRepository.findAll().size();

        // Create the CourseHistory

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCourseHistoryMockMvc.perform(put("/api/course-histories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(courseHistory)))
            .andExpect(status().isBadRequest());

        // Validate the CourseHistory in the database
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCourseHistory() throws Exception {
        // Initialize the database
        courseHistoryService.save(courseHistory);

        int databaseSizeBeforeDelete = courseHistoryRepository.findAll().size();

        // Delete the courseHistory
        restCourseHistoryMockMvc.perform(delete("/api/course-histories/{id}", courseHistory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CourseHistory> courseHistoryList = courseHistoryRepository.findAll();
        assertThat(courseHistoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CourseHistory.class);
        CourseHistory courseHistory1 = new CourseHistory();
        courseHistory1.setId(1L);
        CourseHistory courseHistory2 = new CourseHistory();
        courseHistory2.setId(courseHistory1.getId());
        assertThat(courseHistory1).isEqualTo(courseHistory2);
        courseHistory2.setId(2L);
        assertThat(courseHistory1).isNotEqualTo(courseHistory2);
        courseHistory1.setId(null);
        assertThat(courseHistory1).isNotEqualTo(courseHistory2);
    }
}
