# Testing and Issue Control Plan
## Testing

Tests will be done before each push to the main server repository, and must follow some pass rules before pushing:
* Any tests that pass when the branch was made **MUST NOT** fail when the branch is merged into the master branch.
* Any bugs that stop previously passed tests from passing **MUST** be fixed before merging. 
* Tests **MUST** pass on another team member's machine before merging with the master branch. 
* After merging, the master branch **MUST** pass all previously passed tests or the merge will be reverted and fixed.
* Tests are to be implemented as JUnit tests, aiming for a high level of code and branch coverage. 
* Any functionality that is found not to be tested should have a test written for it and added here; with a note of when it was added. This note should be included in the amendments section at the end of the document to track changes.
### Planned Tests
* **AppController**
    * Attempt login with valid details (all user types)
        * Passes if all log in sucessfully.
    * Attempt login with invalid empNo (all user types)
        * Passes if all login attempts denied.
    * Attempt login with invalid empNo (all user types)
        * Passes if all login attempts denied.
    * Log out of the system after logging in. 
        * Passes if the user is logged in before the command and logged out after. 
    * Set basic access after logging in. (all user types)
        * Passes if logged in user now has basic system access.
    * Check access level in class matches database. (all user types)
        * Passes if access levels match.
    * Check if the system found the right user in the HR database.
        * Passes if the user from the HR Database has the same empID used to log in.

* **AuthServer**
    * Authenticate with valid details (all user types)
        * Passes if right access level is returned. 
    * Authenticate with wrong empNo and password (and cases) (all user types)
        * Passes if authenticate returns "denied".
    * Insert new login into the database.
        * Passes if new user is authenticated. 
    * Delete a login from the database.
        * Passes if deleted login no longer authenticates. 

* **HRDatabase - Personal Details**
    * Get an instance of all user types from the database. 
        * Passes if a user object with all the correct information is returned.
    * Add a new user to the HRDatabase.
        * Passes if a new user can be added only once. 
    * Read a newly added personal details document from the database as HR employee.
        * Passes if the new document has the right staff ID. 
    * Read an employee's own personal details document.
        * Passes if correct document is returned. 
    * Read document for non-existing employee
        * Passes if null returned. 
    * Create a new personal details document as other employee.
        * Passes if create document fails. 
    * Create a new personal details document as HR employee. 
        * Passes if the new document can only be added once. 
    * Amend a personal details document as HR employee.
        * Passes if new values are stored in the document. 
    * Amend own personal details document.
        * Passes if document amended. 
___

* **Create new Review Record**
    * Create document as HR employee.
        * Passes if form is saved successfully to database. 
            
        
* **Read Review Record**
    * Read document pertaining to logged in employee.
        * Passes if document is successfully delivered.
    * Read document pertaining to employee managed by logged in employee.
        * Passes if document is delivered successfully. 
    * Read document pertaining to employee being reviewed by logged in employee.
        * Passes if document is delivered successfully. 
    * Read document that pertains to a non-managed employee as manager.
        * Passes if authorisation fails and document is not delivered. 
    
    
* **Amend Review Record**
    * Amend document that is not completed as reviewer.
        * Passes if edited document is saved to the database. 
    * Amend incomplete document as reviewed employee.
        * Passes if edited document is saved to database.


* **Read Past Completed Review Records**
    * Read documents as employee.
        * Passes if employee is presented with all previous review documents that pertain to them.
    * Read documents as reviewer of employee documents pertain to.
        * Passes if documents are delivered successfully.


* **Perform Review**
    * Attempt review outside of review period (2 weeks from anniversary of employment).
        * Passes if request is rejected and nothing is changed. 
    * Perform review within review period, review is signed off.
        * Passes if participants are presented with past review documents (if they exist), review record has been amended, and HR has been informed.
    * Perform review within review period, no conclusion. 
        * Passes if no changes are made to the document and a new meeting is scheduled. 


* **Allocate Reviewer**
    * HR employee allocates reviewers to an employee review. 
        * Passes if the direct manager of the employee is a reviewer and another higher level employee or director is a reviewer, and both have been informed of the date. 
    * HR employee attempts to allocate reviewers to a review, but no 2<sup>nd</sup> reviewer was selected.
        * Passes if no reviewers were allocated. 


## Bug Reporting
Bug reports are to be handled using the 'Issue' system on GitLab. 

The person who is making the report should assign it to the person responsible for that part of the program. 
This information can be found by referring to the latest meeting documents, which will contain the person who is assigned to that part of the project. 

## Amendments
* 2019-02-11:  Document amended to include the type of tests being used (JUnit) and mentions the amendments section. 
* 2019-03-03: Document will now be split by class once the test classes are written. Unwritten classes will remain by use case until covered elsewhere. 
* 2019-03-04: Test cases added for HRDatabase. 