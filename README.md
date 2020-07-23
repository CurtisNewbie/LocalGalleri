# LocalGalleri

A basic webapp (**Spring Boot** + **Angular**) for image sharing. It essentially scans all the images in a directory, and exposes them as resources via REST and **HATEOAS** to the Angular frontend. Angular then uses a **Virtual Scroll CDK** to fetch these images while the user is 'scrolling up/down'.

**Prerequisite:**

- Java 8

# How To Use It

Angular build is bundled inside the jar, so all you need to do is to execute the following command:

> java -jar galleriback-0.0.1.jar

The default directory being scanned is `/galleriImages`, which is also a fallback option when the custom configuration is invalid. It's almost certain that you want to specify a directory that you prefer, you can add an argument (`--scan.dir=`) when you run the app as follows:

> java -jar galleriback-0.0.1.jar --scan.dir=my/images/to/be/shared

# Demo

<img src="https://user-images.githubusercontent.com/45169791/88256323-922d9e00-cced-11ea-8c88-22a6d47e6f0a.gif" width="800">
