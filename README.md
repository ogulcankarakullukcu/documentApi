# Document Api
This project provides an API for file upload, download, listing, renaming, and deletion operations. The project is developed using Java 17 and Spring Boot 3, integrated with the H2 database.

- Java 17
- Maven
- Spring Boot 3
- H2 Database
- JWT Authentication

# File Upload

- Send the created POST request to the server where the application is running.
- The server retrieves the path where the file is saved, its size, name, and extension.
- The obtained file information is stored in a relational database table.
- The file size can be a maximum of 5MB and its extension must be one of the following: png, jpeg, jpg, docx, pdf, xlsx.
- Files that do not comply with these rules cannot be saved, and the system return an error message.

# File Download

- To download a file, the system uses a GET method where the file's ID is provided as a parameter.
- The server processes this request by retrieving the file corresponding to the given ID.
- The file is then converted into a byte array, which is included in the response body.

# Swagger UI:

![image](https://github.com/ogulcankarakullukcu/documentApi/assets/36894498/669f8ca6-ec02-4215-838e-654f6bd7631c)

# Postman Collection:

![image](https://github.com/ogulcankarakullukcu/documentApi/assets/36894498/024be284-7b47-400a-95e5-6212ec2e11c9)
