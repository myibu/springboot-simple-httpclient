# springboot-simple-httpclient
support to use simple httpclient in springboot with annotation


## Implements

Scan interface with HttpClient annotated, then use simple httpclient to create beans in spring framework.

## Installation
```bash
<dependency>
  <groupId>com.github.myibu</groupId>
  <artifactId>springboot-simple-httpclient</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Examples

Assume than you have defined a interface annotated with HttpClient annotation as following:
```java
@SpringBootAppliction
@EnableHttpClient
public class App {
   public static main(String[] args) {
       SpringApplication.run(App.class, args);
   }
}
```
