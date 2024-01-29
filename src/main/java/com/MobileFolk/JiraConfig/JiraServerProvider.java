package com.MobileFolk.JiraConfig;

import net.rcarz.jiraclient.*;
import net.rcarz.jiraclient.Issue.FluentCreate;

public class JiraServerProvider {
    public JiraClient jira;
    public String project;

    public JiraServerProvider(String jiraUrl, String username, String password, String project) {
        BasicCredentials creds = new BasicCredentials(username, password);
        jira = new JiraClient(jiraUrl, creds);
        this.project = project;

    }

    public void createJiraTicket(String issueType, String summary, String description, String reporterName) {
        try {
            FluentCreate fluentCreate = jira.createIssue(project, issueType);
            fluentCreate.field(Field.SUMMARY, summary);
            fluentCreate.field(Field.DESCRIPTION, description);
            Issue newIssue = fluentCreate.execute();

            System.out.println("New issue create in jira with ID: " + newIssue);
        } catch (JiraException e) {
            throw new RuntimeException(e);
        }
    }
}
