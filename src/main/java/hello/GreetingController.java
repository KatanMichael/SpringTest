package hello;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController
{

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

        return p;
    }

    @GetMapping("/home")
    public String home(@RequestParam(name = "name", defaultValue = " ") String name ,Model model)
    {
        model.addAttribute("name",name);
        return "Welcome Home!";
    }

    @RequestMapping("/testForm")
    public Person testForm(@RequestParam(name = "name", defaultValue = " ") String name,
                           @RequestParam(name = "age", defaultValue = "0") String age)
    {
        int inAge;

        try
        {
            inAge = Integer.parseInt(age);
        }catch (NumberFormatException e)
        {
            inAge = 0;
        }

        Person p = new Person(inAge,24,name);

        Firestore db = FirestoreClient.getFirestore();

        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("age", age);

        final ApiFuture<DocumentReference> users = db.collection("Users").add(data);

        return p;
    }

    @RequestMapping("/personByAge")
    public List<Person> getPersonsByAge(@RequestParam(name = "age", defaultValue = "0") String age)
    {
        List<Person> ppl = new ArrayList<>();
        Firestore db = FirestoreClient.getFirestore();

        final ApiFuture<QuerySnapshot> query = db.collection("Users").whereEqualTo("age",age)
                .get();
        QuerySnapshot queryDocumentSnapshots = null;
        try {
            queryDocumentSnapshots = query.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if(queryDocumentSnapshots != null)
        {
            for(DocumentSnapshot document: queryDocumentSnapshots.getDocuments())
            {
                String name = (String)document.get("name");
                int inAge = Integer.parseInt((String)document.get("age"));
                ppl.add(new Person(123,inAge,name));
            }
        }

        return ppl;
    }

}