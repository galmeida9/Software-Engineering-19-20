// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />

/* LOGIN Commands */
/* Demo Login - Admin */
Cypress.Commands.add('demoAdminLogin', () => {
    cy.visit('/');
    cy.get('[data-cy="adminButton"]').click();
    cy.contains('Administration').click();
    cy.contains('Manage Courses').click()
});

/* Demo Login - Teacher/Student */
Cypress.Commands.add('demoLogin', (type) => {
    cy.visit('/');
    cy.get('[data-cy="' + type + 'Button"]').click();
});
/* ----------------------- */

/* Teacher Commands */
/* Create a clarificationAnswer given a message*/
Cypress.Commands.add('createClarificationAnswer', (title, message) => {
    cy.get('[data-cy="clarificationsButton"]').click();
    cy.contains('List all').click();
    cy.contains(title)
      .parent()
      .should('have.length', 1)
      .find('[data-cy="viewClarification"]')
      .click();
    if (message != null) {
        cy.get('[data-cy="answerClarification"]').type(message);
    }
    cy.get('[data-cy="sendClarificationAnswerButton"]').click();
    cy.get('[data-cy="closeClarificationDialog"]').click();
});

/* ----------------------- */

/* Student Commands */

/* Make and solve a quiz */
Cypress.Commands.add('makeAndSolveQuiz', () => {
    cy.get('[data-cy="quizzesButton"]').click();
    cy.contains('Create').click();
    cy.get('[data-cy="createQuizButton"]').click();
    cy.get('[data-cy="endQuizButton"]').click();
    cy.get('[data-cy="endQuizImSureButton"]').click();
});

/* Get first solved quiz */
Cypress.Commands.add('firstSolvedQuiz', () => {
    cy.get('[data-cy="quizzesButton"]').click();
    cy.contains('Solved').click();
    cy.get('.list-row').eq(0).should('have.class', 'list-row').click();
});

/* Get to clarification list */
Cypress.Commands.add('clarificationList', () => {
    cy.get('[data-cy="clarificationsButton"]').click();
    cy.contains('List all').click();
});

/* Create a clarification given a message */
Cypress.Commands.add('createClarification', (clarificationMessage) => {
    cy.get('[data-cy="createClarificationButton"]').click();
    if (clarificationMessage != null)
        cy.get('[data-cy="clarificationText"]').type(clarificationMessage);
    cy.get('[data-cy="sendClarificationButton"]').click();
});

/* Check if clarification is answered or not given a title */
Cypress.Commands.add('checkClarificationAnswered', (title, isAnswered) => {
    let icon = (isAnswered) ? 'answered' : 'notAnswered';
    cy.contains(title).parent()
      .children().should('have.length', 5)
      .find('[data-cy="' + icon + '"]').should('have.length', 1);
});

/* Check if clarification answer exists*/
Cypress.Commands.add('checkForClarificationAnswer', (title, message) => {
    cy.contains(title)
      .parent()
      .should('have.length', 1)
      .find('[data-cy="viewClarification"]')
      .click();
    cy.contains(message);
});

/* ----------------------- */

/* ADMIN Commands */
/* Create a course execution given a name, acronym and academic Term */
Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click();
    cy.get('[data-cy="Name"]').type(name);
    cy.get('[data-cy="Acronym"]').type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click()
});

/* Create a course execution from another course execution given a name, acronym and academic Term */
Cypress.Commands.add('createFromCourseExecution', (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="Acronym"]').type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click()
});

/* Delete a course execution given an acronym */
Cypress.Commands.add('deleteCourseExecution', (acronym) => {
    cy.contains(acronym)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="deleteCourse"]')
      .click();
});
/* ----------------------- */

/* Universal Commands */
/* Close an error message */
Cypress.Commands.add('closeErrorMessage', () => {
    cy.contains('Error')
      .parent()
      .find('button')
      .click();
});

/* Close a success message given the message */
Cypress.Commands.add('closeSuccessMessage', (successMessage) => {
    cy.contains(successMessage)
      .parent()
      .find('button')
      .click();
});

/* Logout */
Cypress.Commands.add('logout', () => {
    cy.get('[data-cy="logout"]').click();
});

/* ----------------------- */

