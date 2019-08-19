const functions = require('firebase-functions');

const admin = require('firebase-admin')

const firebaseConfig = {
    apiKey: "AIzaSyBLR6Sx_UZh8W1doAkDD8IdH8MW2JT0Euw",
    authDomain: "triviaapi-d1d92.firebaseapp.com",
    databaseURL: "https://triviaapi-d1d92.firebaseio.com",
    projectId: "triviaapi-d1d92",
    storageBucket: "triviaapi-d1d92.appspot.com",
    messagingSenderId: "169366846849",
    appId: "1:169366846849:web:f6f7686449ffc153"
  };

admin.initializeApp(firebaseConfig);

let db= admin.firestore();

exports.helloWorld = functions.https.onRequest((request, response) => 
{
    var age = Math.random()*100;
    const data = 
    {
        "age" : age,
        "name": "RandomName"
    };

    db.collection("Users").add(data).then(ref =>
        {
            response.send(ref);
            return ref;
        }).catch(error =>
            {
                response.send(error);
            });
});


exports.updataMichael = functions.firestore.document('Users/{UserId}').onCreate((snapshot, content) =>
    {
        const newValue = snapshot.data();

        const name = newValue.name;

        if(name === "Michael")
        {
            var newData =
            {
                "age" : 29,
                "name": "Michael"
            };

            snapshot.ref.update(newData).then(ref =>
                {
                    return ref;
                }).catch(error => 
                    {
                        console.log(error);
                    });
        }



    });


    exports.updateExistsUser = functions.firestore.document("Users/{userId}")
    .onCreate((snapshot, context) =>
    {
        const data = snapshot.data();
        const userAddedName = data.name;

        var userRef = db.collection("Users");
        var query = userRef.where("name" , "=", userAddedName);

        query.get().then((querySnapshot) => {
            
            if(!querySnapshot.empty)
            {
                var user = querySnapshot.docs[0];

                var age = parseInt(snapshot.data().age) ;
                age += parseInt(user.data().age);

                const updateData = 
                {
                    "name" : userAddedName,
                    "age" : age
                    
                };

                // eslint-disable-next-line promise/no-nesting
                user.ref.update(updateData).then(ref =>
                {
                    console.log(ref);
                    return ref;
                }).catch(error)
                {
                    console.log(error);
                }

            }

            return 0;
          }).catch(outError)
          {
              console.log(outError);
          }

          snapshot.ref.delete();
    });