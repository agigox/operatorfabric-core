/* Copyright (c) 2020, RTE (http://www.rte-france.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */


package org.lfenergy.operatorfabric.thirds.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.lfenergy.operatorfabric.test.AssertUtils.assertException;
import static org.lfenergy.operatorfabric.thirds.model.ResourceTypeEnum.CSS;
import static org.lfenergy.operatorfabric.thirds.model.ResourceTypeEnum.I18N;
import static org.lfenergy.operatorfabric.thirds.model.ResourceTypeEnum.TEMPLATE;
import static org.lfenergy.operatorfabric.utilities.PathUtils.copy;
import static org.lfenergy.operatorfabric.utilities.PathUtils.silentDelete;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lfenergy.operatorfabric.thirds.application.IntegrationTestApplication;
import org.lfenergy.operatorfabric.thirds.model.Third;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import lombok.extern.slf4j.Slf4j;


/**
 * <p></p>
 * Created on 16/04/18
 *
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {IntegrationTestApplication.class})
@Slf4j
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ThirdsServiceShould {

  private static Path testDataDir = Paths.get("./build/test-data/thirds-storage");
  @Autowired
  private ThirdsService service;

  @BeforeEach
  void prepare() throws IOException {
    copy(Paths.get("./src/test/docker/volume/thirds-storage"), testDataDir);
    service.loadCache();
  }

  @AfterAll
  static void dispose() throws IOException {
    if (Files.exists(testDataDir))
      Files.walk(testDataDir, 1).forEach(p -> silentDelete(p));
  }

  @Test
  void listThirds() {
    assertThat(service.listThirds()).hasSize(2);
  }

  @Test
  void fetch() {
    Third firstThird = service.fetch("first");
    assertThat(firstThird).hasFieldOrPropertyWithValue("version", "v1");
  }

  @Test
  void fetchWithVersion() {
    Third firstThird = service.fetch("first", "0.1");
    assertThat(firstThird).hasFieldOrPropertyWithValue("version", "0.1");
  }

  @Test
  void fetchCss() throws IOException {
    File styleFile = service.fetchResource("first", CSS, "style1").getFile();
    assertThat(styleFile.getParentFile())
       .isDirectory()
       .doesNotHave(
          new Condition<>(f -> f.getName().equals("fr") || f.getName().equals("en"),
             "parent directory should not be a locale directory"));
    assertThat(styleFile)
       .exists()
       .isFile()
       .hasName("style1.css")
       .hasContent(".bold {\n" +
          "    font-weight: bold;\n" +
          "}");
    styleFile = service.fetchResource("first", CSS, "0.1", "style1").getFile();
    assertThat(styleFile)
       .exists()
       .isFile()
       .hasName("style1.css")
       .hasContent(".bold {\n" +
          "    font-weight: bolder;\n" +
          "}");
    styleFile = service.fetchResource("first", CSS, "0.1", "fr", "style1").getFile();
    assertThat(styleFile)
       .exists()
       .isFile()
       .hasName("style1.css")
       .hasContent(".bold {\n" +
          "    font-weight: bolder;\n" +
          "}");
  }

  @Test
  void fetchTemplate() throws IOException {
    File templateFile = service.fetchResource("first", TEMPLATE, null,"fr","template1").getFile();
    assertThat(templateFile.getParentFile()).isDirectory().hasName("fr");
    assertThat(templateFile)
       .exists()
       .isFile()
       .hasName("template1.handlebars")
       .hasContent("{{service}} fr");
    templateFile = service.fetchResource("first", TEMPLATE, "0.1", "fr", "template").getFile();
    assertThat(templateFile)
       .exists()
       .isFile()
       .hasName("template.handlebars")
       .hasContent("{{service}} fr 0.1");
    templateFile = service.fetchResource("first", TEMPLATE, "0.1", "en", "template").getFile();
    assertThat(templateFile)
       .exists()
       .isFile()
       .hasName("template.handlebars")
       .hasContent("{{service}} en 0.1");
  }

  @Test
  void fetchI18n() throws IOException {
    File i18nFile = service.fetchResource("first", I18N,null,"fr", null).getFile();
    assertThat(i18nFile)
       .exists()
       .isFile()
       .hasName("fr.json")
       .hasContent("card.title=\"Titre $1\"");
    i18nFile = service.fetchResource("first", I18N, "0.1", "fr", null).getFile();
    assertThat(i18nFile)
       .exists()
       .isFile()
       .hasName("fr.json")
       .hasContent("card.title=\"Titre $1 0.1\"");
    i18nFile = service.fetchResource("first", I18N, "0.1", "en", null).getFile();
    assertThat(i18nFile)
       .exists()
       .isFile()
       .hasName("en.json")
       .hasContent("card.title=\"Title $1 0.1\"");
  }

  @Test
  void fetchResourceError() {
    assertException(FileNotFoundException.class).isThrownBy(() ->
       service.fetchResource("what",
          TEMPLATE,
          "0.1",
          null,
          "template"));
    assertException(FileNotFoundException.class).isThrownBy(() ->
       service.fetchResource("first",
          TEMPLATE,
          "0.2",
          null,
          "template"));
    assertException(FileNotFoundException.class).isThrownBy(() ->
       service.fetchResource("first",
          CSS,
          "0.1",
          null,
          "styleWhat"));
    assertException(FileNotFoundException.class).isThrownBy(() ->
       service.fetchResource("first",
          TEMPLATE,
          "0.1",
          null,
          "template1")
          .getInputStream()
    );

    assertException(FileNotFoundException.class).isThrownBy(() ->
       service.fetchResource("first",
          TEMPLATE,
          "0.1",
          "de",
          "template")
          .getInputStream()
    );
  }

  @Nested
  class CreateContent {
    @RepeatedTest(2)
    void updateThird() throws IOException {
      Path pathToBundle = Paths.get("./build/test-data/bundles/second-2.0.tar.gz");
      try (InputStream is = Files.newInputStream(pathToBundle)) {
        Third t = service.updateThird(is);
        assertThat(t).hasFieldOrPropertyWithValue("name", "second");
        assertThat(t).hasFieldOrPropertyWithValue("version", "2.0");
        assertThat(t.getProcesses().size()).isEqualTo(1);
        assertThat(t.getProcesses().get("testProcess").getStates().size()).isEqualTo(1);
        assertThat(t.getProcesses().get("testProcess").getStates().get("firstState").getDetails().size()).isEqualTo(1);
        assertThat(t.getProcesses().get("testProcess").getStates().get("firstState").getActions().size()).isEqualTo(1);
        assertThat(service.listThirds()).hasSize(3);
      } catch (IOException e) {
        log.trace("rethrowing exception");
        throw e;
      }
    }
    
    @Nested
    class DeleteOnlyOneThird {
    	
    	static final String bundleName = "first";
    	
    	static final String CONFIG_FILE_NAME = "config.json";
    	
    	@BeforeEach
		void prepare() throws IOException {
		  if (Files.exists(testDataDir))
		      Files.walk(testDataDir, 1).forEach(p -> silentDelete(p));
		    copy(Paths.get("./src/test/docker/volume/thirds-storage"), testDataDir);
		    service.loadCache();
		}

    	@Test
        void deleteBundleByNameAndVersionWhichNotBeingDeafult() throws Exception {
    		Path bundleDir = testDataDir.resolve(bundleName);
    		Path bundleVersionDir = bundleDir.resolve("0.1");
    		Assertions.assertTrue(Files.isDirectory(bundleDir));
    		Assertions.assertTrue(Files.isDirectory(bundleVersionDir));
            service.deleteVersion(bundleName,"0.1");
            Assertions.assertNull(service.fetch(bundleName, "0.1"));
            Third third = service.fetch(bundleName);
            Assertions.assertNotNull(third);
            Assertions.assertFalse(third.getVersion().equals("0.1"));
            Assertions.assertTrue(Files.isDirectory(bundleDir));
            Assertions.assertFalse(Files.isDirectory(bundleVersionDir));
    	}
    	
    	@Test
        void deleteBundleByNameAndVersionWhichBeingDeafult1() throws Exception {
    		Path bundleDir = testDataDir.resolve(bundleName);
    		Third third = service.fetch(bundleName);
    		Assertions.assertTrue(third.getVersion().equals("v1"));
    		Path bundleVersionDir = bundleDir.resolve("v1");
    		Path bundleNewDefaultVersionDir = bundleDir.resolve("0.1");
    		FileUtils.touch(bundleNewDefaultVersionDir.toFile());//this is to be sure this version is the last modified
    		Assertions.assertTrue(Files.isDirectory(bundleDir));
    		Assertions.assertTrue(Files.isDirectory(bundleVersionDir));    		
            service.deleteVersion(bundleName,"v1");
            Assertions.assertNull(service.fetch(bundleName, "v1"));
            third = service.fetch(bundleName);
            Assertions.assertNotNull(third);
            Assertions.assertTrue(third.getVersion().equals("0.1"));
            Assertions.assertTrue(Files.isDirectory(bundleDir));
            Assertions.assertFalse(Files.isDirectory(bundleVersionDir));
            Assertions.assertTrue(Files.isDirectory(bundleNewDefaultVersionDir));
            Assertions.assertTrue(FileUtils.contentEquals(bundleDir.resolve(CONFIG_FILE_NAME).toFile(),
    				bundleNewDefaultVersionDir.resolve(CONFIG_FILE_NAME).toFile()));
    	}
    	
    	@Test
        void deleteBundleByNameAndVersionWhichBeingDeafult2() throws Exception {
    		Path bundleDir = testDataDir.resolve(bundleName);
    		final Third third = service.fetch(bundleName);
    		Assertions.assertTrue(third.getVersion().equals("v1"));
    		Path bundleVersionDir = bundleDir.resolve("v1");
    		Path bundleNewDefaultVersionDir = bundleDir.resolve("0.5");
    		FileUtils.touch(bundleNewDefaultVersionDir.toFile());//this is to be sure this version is the last modified
    		Assertions.assertTrue(Files.isDirectory(bundleDir));
    		Assertions.assertTrue(Files.isDirectory(bundleVersionDir));    		
            service.deleteVersion(bundleName,"v1");
            Assertions.assertNull(service.fetch(bundleName, "v1"));
            Third _third = service.fetch(bundleName);
            Assertions.assertNotNull(_third);
            Assertions.assertTrue(_third.getVersion().equals("0.5"));
            Assertions.assertTrue(Files.isDirectory(bundleDir));
            Assertions.assertFalse(Files.isDirectory(bundleVersionDir));
            Assertions.assertTrue(Files.isDirectory(bundleNewDefaultVersionDir));
            Assertions.assertTrue(FileUtils.contentEquals(bundleDir.resolve(CONFIG_FILE_NAME).toFile(),
    				bundleNewDefaultVersionDir.resolve(CONFIG_FILE_NAME).toFile()));
    	}
    	
    	@Test
        void deleteBundleByNameAndVersionWhichNotExisting() throws Exception {
    		Assertions.assertThrows(FileNotFoundException.class, () -> {service.deleteVersion(bundleName,"impossible_someone_really_so_crazy_to_give_this_name_to_a_version");});
    	}
    	
    	@Test
        void deleteBundleByNameWhichNotExistingAndVersion() throws Exception {
    		Assertions.assertThrows(FileNotFoundException.class, () -> {service.deleteVersion("impossible_someone_really_so_crazy_to_give_this_name_to_a_bundle","1.0");});
    	}
    	
    	@Test
        void deleteBundleByNameAndVersionHavingOnlyOneVersion() throws Exception {
    		Path bundleDir = testDataDir.resolve("third");
    		Assertions.assertTrue(Files.isDirectory(bundleDir));
            service.deleteVersion("third","2.1");
            Assertions.assertNull(service.fetch("third","2.1"));
            Assertions.assertNull(service.fetch("third"));            
            Assertions.assertFalse(Files.isDirectory(bundleDir));
    	}
    	
    	@Test
        void deleteGivenBundle() throws Exception {
    		Path bundleDir = testDataDir.resolve(bundleName);
    		Assertions.assertTrue(Files.isDirectory(bundleDir));
            service.delete(bundleName);
            Assertions.assertFalse(Files.isDirectory(bundleDir));
        }

	    @Nested
	    class DeleteContent {
	      @Test
	      void clean() throws IOException {
	        service.clear();
	        assertThat(service.listThirds()).hasSize(0);
	      }
	    }
    }
  }
}
