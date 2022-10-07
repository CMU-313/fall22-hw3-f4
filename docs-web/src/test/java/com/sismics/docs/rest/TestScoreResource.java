package com.sismics.docs.rest;

import java.util.Date;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;

import org.junit.Assert;
import org.junit.Test;

import com.sismics.util.filter.TokenBasedSecurityFilter;

public class TestScoreResource extends BaseJerseyTest {
    /**
     * Test the score resource.
     * 
     * @throws Exception e
    */
    @Test
    public void testScoreResourceReponse() throws Exception {
        // Login doc
        clientUtil.createUser("testUser");
        String testUserToken = clientUtil.login("testUser");

        // Create a tag
        JsonObject json = target().path("/tag").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
                .put(Entity.form(new Form()
                        .param("name", "SuperTag")
                        .param("color", "#ffff00")), JsonObject.class);
        String tagId = json.getString("id");

        // Create a document with document1
        long createDate = new Date().getTime();
        json = target().path("/document").request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
                .put(Entity.form(new Form()
                        .param("title", "My super title document 1")
                        .param("description", "My super description for document 1")
                        .param("subject", "Subject document 1")
                        .param("identifier", "Identifier document 1")
                        .param("publisher", "Publisher document 1")
                        .param("format", "Format document 1")
                        .param("source", "Source document 1")
                        .param("type", "Software")
                        .param("coverage", "Greenland")
                        .param("rights", "Public Domain")
                        .param("tags", tagId)
                        .param("language", "eng")
                        .param("create_date", Long.toString(createDate))
                    ), JsonObject.class);
        String docId = json.getString("id");
        
        // Put a skill score to a document
        json = target().path("/skillScore").request()
        .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
        .put(Entity.form(new Form()
                .param("id", docId)
                .param("skillScore", "3")
            ), JsonObject.class);
        
        // Test the skill score response values
        String skillScoreVal = json.getString("score");
        Assert.assertEquals("3", skillScoreVal);
        
        String skillReviewerVal = json.getString("reviewer");
        Assert.assertEquals("testUser", skillReviewerVal);

        // Put an experience score to a document
        json = target().path("/experienceScore").request()
        .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
        .put(Entity.form(new Form()
                .param("id", docId)
                .param("experienceScore", "2")
            ), JsonObject.class);
        
        // Test the experience score response values
        String experienceScoreVal = json.getString("score");
        Assert.assertEquals("2.5", experienceScoreVal);
        
        String experienceReviewerVal = json.getString("reviewer");
        Assert.assertEquals("testUser", experienceReviewerVal);

        // Put gpa to a document
        json = target().path("/GPA").request()
        .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
        .put(Entity.form(new Form()
                .param("id", docId)
                .param("GPA", "1.5")
            ), JsonObject.class);
        
        // Test the experience score response values
        String GPAVal = json.getString("GPA");
        Assert.assertEquals("5", GPAVal);
        
        String GPAReviewerVal = json.getString("reviewer");
        Assert.assertEquals("testUser", GPAReviewerVal);

        // get the list of documents 
        json = target().path("/document/" + docId)
                .request()
                .cookie(TokenBasedSecurityFilter.COOKIE_NAME, testUserToken)
                .get(JsonObject.class);
        
        // Test the stored scores for skill, experience, and gpa
        String storedSkillScore = json.getString("skillScore");
        Assert.assertEquals("3", storedSkillScore);

        String storedExperienceScore = json.getString("experienceScore");
        Assert.assertEquals("2", storedExperienceScore);

        String storedGPA = json.getString("GPA");
        Assert.assertEquals("1.5", storedGPA);
    }
}


