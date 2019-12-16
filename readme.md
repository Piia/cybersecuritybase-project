LINK: https://github.com/Piia/cybersecuritybase-project 

# INSTRUCTIONS:

Instructions and commands to install and run the project using command line. You must have git and mvn installed in order to run the commands.

## 1. Clone the project to your computer:
Run command: `git clone https://github.com/Piia/cybersecuritybase-project.git`

## 2. Enter the project folder:
Run command: `cd cybersecuritybase-project`

## 3. Build the project:
Run command: `mvn package`

## 4. Start running:
Run command: `java -jar target/cybersecuritybase-project-1.0-SNAPSHOT.jar`

## 5. Use your browser and navigate to http://localhost:8080 


# FLAW 1: Cross-Site Scripting (XSS)

Cross-site scripting security flaw allows any authenticated user to submit their scripts with the sign up form. When any user loads the page containing the list of signups, the script is executed on their browser. Depending on the script, this might have serious consequences. The following steps include a safe alert-script for testing purposes.

## Steps to reproduce:

1. Browse to http://localhost:8080 and log in (username: ted, password: ted)
2. Type `<script>alert('attack');</script>` to the name field
3. Click Submit
4. Your script creates a popup containing word “attack”

This security flaw is due to using an unsafe template code `th:utext`. To fix this, change `th:utext` to `th:text` in done.html. Thymeleaf's utext is sort from “unescaped text”, whereas th:text escapes html-tags from evil submits so that any script will not become executed.


# FLAW 2: Security Misconfiguration

I disabled the cross site request forgery protection of Spring Security Configuration. This unfortunate misconfiguration allows evil websites to send requests and form data to our endpoints without us knowing the difference, and they have the cookies from the user. On top of that, the existing XSS-flaw (FLAW 1) allows users to submit scripts and hidden forms to trigger request forgeries for all visiting users. When enabled, the cross site request forgery protection configuration will add a hidden field to all application forms that include `_csrf`-prefixed name and value tokens. These tokens are then checked on POST/PUT/PATCH/DELETE-requests to verify that the data comes from our own forms.

## Steps to reproduce:

1. Browse to http://localhost:8080 and log in (username: ted, password: ted)
2. Open your development tools and inspect the form component 
3. See that it does not contain a hidden field with `_csrf`-prefixed name and value

Spring Security enables cross site request forgery protection by default, so this security flaw can be fixed by removing the line `http.csrf().disable();` from SecurityConfiguration.java.


# FLAW 3: Using Components with Known Vulnerabilities

This project contains several dependencies that have known vulnerabilities. I added the plugin dependency-check that checks the dependencies and report their vulnerabilities.

## Steps to reproduce:

1. Open terminal and go to the project folder
2. Run command `mvn dependency-check:check`
3. Dependency-check prints the report of dependencies with known vulnerabilities

To fix this security flaw, the dependencies should be updated to pom.xml. If a dependency is not supported anymore, you should seek for an alternative. This might be a big task, since the interfaces and functionalities can change in major version number jumps, and the compatibility of the dependencies need to be checked again.


# FLAW 4: Broken Access Control

I added required authentication to the security configuration. There are now two users:
(username: ted, password: ted, authority: USER)
(username: admin, password: admin, authority: ADMIN)
The user “admin” has been granted admin rights, but the path /admin does not require the admin role. The admin path shows the addresses, and deletion-buttons for the signups. Also, the deletion endpoint does not require the admin role.

## Steps to reproduce:

1. Browse to http://localhost:8080 and log in (username: ted, password: ted)
2. Add one signup with name and address
3. Go to path /admin
4. You will see the signup addresses and “delete”-buttons, even though ted is not admin 
5. Press one delete-button and see how it becomes deleted

I have provided the missing access control configuration as a commented line in SecurityConfiguration.java: `.antMatchers("/admin").hasAuthority("ADMIN")`. Uncomment it. Then, uncomment the line `@PreAuthorize("hasAuthority('ADMIN')")` in SignupController.java.


# FLAW 5: Sensitive Data Exposure

Sensitive data has already been exposed in the previous security flaw, since the /admin-path was not controlled correctly. There is also another way that the address information is exposed to the regular users: In the list of signups, the addresses are hidden by using inline-styling: “display:none”. This means, that the addresses are sent to the user's browser, and they can see those from the source code or with browser's developer tools.

## Steps to reproduce:

1. Browse to http://localhost:8080 and log in (username: ted, password: ted)
2. Add one signup with name and address
3. Open developer tools and inspect the list element that contains the name
4. See that it also contains a hidden `<span style=”display:none”>`-element that contains the address that should not be shown for a regular user

This security flaw can be fixed by changing the `th:style`-code in done.html. The delete-button on the next line of the code has an example of `th:if`-code that conditionally renders the component. Then the sensitive address information is not rendered and it is not sent to the user's browser.
