package hello;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name)
    {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }


    @RequestMapping("/randomPerson")
    public Person randomPerson(@RequestParam(value="name", defaultValue = " ") String name)
    {
        Random random = new Random();
        Person p = new Person(random.nextInt(1000),random.nextInt(120),name);

        GoogleCredentials credentials = null;
        try {
            credentials = GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId("triviaapi-d1d92")
                .build();
        FirebaseApp.initializeApp(options);

        Firestore db = FirestoreClient.getFirestore();

        final ApiFuture<DocumentReference> peoples = db.collection("Peoples").add(p);

        try {
            System.out.println(peoples.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return p;
    }

    @GetMapping("/home")
    public String home(@RequestParam(name = "name", defaultValue = " ") String name ,Model model)
    {
        model.addAttribute("name",name);



        return "Welcome Home!";
    }


}