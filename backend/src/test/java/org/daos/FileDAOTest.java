package org.daos;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.daos.FileDAO;
import org.junit.jupiter.api.*;
import org.persistence.HibernateConfig;
import org.persistence.model.File;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileDAOTest {

    private static EntityManagerFactory emf;
    private static FileDAO fileDAO;

    @BeforeAll
    public static void beforeAll() {
        emf = HibernateConfig.getEntityManagerFactoryConfig(true);
        fileDAO = FileDAO.getInstance(emf);
    }

    @AfterAll
    public static void afterAll() {
        emf.close();
    }

    @BeforeEach
    public void beforeEach() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery("TRUNCATE TABLE File RESTART IDENTITY").executeUpdate();
            em.persist(new File("/folder", "profile-picture", ".jpg"));
            em.persist(new File("/folder", "profile-picture2", ".jpg"));
            em.persist(new File("/folder2", "profile-picture3", ".png"));
            em.getTransaction().commit();
        }
    }

    @AfterEach
    public void afterEach() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM File ").executeUpdate();
            em.getTransaction().commit();
        }
    }

    @Test
    @DisplayName("Testing that entity manager factory is not null.")
    public void testingEntityManagerFactory() {
        // Given

        // When

        // Then
        assertNotNull(emf);
    }

    @Test
    @DisplayName("Testing that FileDAO is not null.")
    public void testFileDAO() {
        // Given

        // When

        // Then
        assertNotNull(fileDAO);
    }

    @Test
    @DisplayName("Testing creating a file.")
    public void createFile(){
        //Given
        File expectedFile = new File("ademnation","adem-picture", ".jpg");
        int expectedId = 4;

        //When
        File createdFile = fileDAO.create(expectedFile);

        //Then
        assertNotNull(createdFile);
        assertEquals(expectedFile.getName(),createdFile.getName());
        assertEquals(expectedFile.getFolderPath(),createdFile.getFolderPath());
        assertEquals(expectedFile.getFileType(), createdFile.getFileType());
        assertEquals(expectedId, createdFile.getId());
    }

    @Test
    @DisplayName("Successfully deleting a file")
    public void deleteFile1(){
        //Given
        int expectedResult = 1;

        //When
        int result = fileDAO.delete(1);

        //Then
        assertEquals(expectedResult, result);
        assertTrue(result > 0);
    }

    @Test
    @DisplayName("Wrongfully deleting a file")
    public void deleteFile2(){
        //Given
        int expectedResult = 0;

        //When
        int result = fileDAO.delete(5);

        //Then
        assertEquals(expectedResult, result);
        assertTrue(result == 0);
        assertFalse(result > 0);
    }

    @Test
    @DisplayName("Getting file by id.")
    public void readFile(){
        //Given
        int expetedRead = 1;
        String expectedName = "profile-picture";

        //When
        File foundFile = fileDAO.getById(1);

        //THen
        assertNotNull(foundFile);
        assertEquals(expetedRead, foundFile.getId());
        assertEquals(foundFile.getName(), expectedName);
    }

    @Test
    @DisplayName("Update file ")
    public void updateFile(){
        //Given
        int getFile = 1;
        File fileExpected;
        try (EntityManager em = emf.createEntityManager()) {
            fileExpected =  em.find(File.class, getFile);
        }
        fileExpected.setFolderPath("halluu");
        fileExpected.setName("hallu-picture");
        fileExpected.setFileType(".png");

        //When
        File fileUpdated = fileDAO.update(fileExpected);

        //Then
        assertNotNull(fileUpdated);
        assertEquals(fileExpected.getName(),fileUpdated.getName());
        assertEquals(fileExpected.getFolderPath(),fileUpdated.getFolderPath());
        assertEquals(fileExpected.getFileType(), fileUpdated.getFileType());
        assertEquals(fileExpected.getId(), fileUpdated.getId());
    }

    @Test
    @DisplayName("Getting all files by type in path")
    public void getAllByTypeInPath(){
        //Given
        int expectedListSize = 2;
        int expectedIdForTheFirstIndex = 1;

        //When
        List<File> allFiles = fileDAO.getAllByTypeInPath("/folder", ".jpg");

        //Then
        assertNotNull(allFiles);
        assertEquals(expectedListSize, allFiles.size());
        assertEquals(expectedIdForTheFirstIndex,allFiles.get(0).getId());
    }

    @Test
    @DisplayName("Getting all files by path")
    public void getAllFilesInPath(){
        //Given
        int expectedListSize = 1;
        int expectedIdForTheFirstIndex = 3;

        //When
        List<File> allFiles = fileDAO.getAllFilesInPath("/folder2");

        //Then
        assertNotNull(allFiles);
        assertEquals(expectedListSize, allFiles.size());
        assertEquals(expectedIdForTheFirstIndex,allFiles.get(0).getId());
    }
}