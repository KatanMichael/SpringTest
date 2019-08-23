/* eslint-disable promise/no-nesting */
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
        // const newValue = snapshot.data();

        // const name = newValue.name;

        // if(name === "Michael")
        // {
        //     var newData =
        //     {
        //         "age" : 29,
        //         "name": "Michael"
        //     };

        //     snapshot.ref.update(newData).then(ref =>
        //         {
        //             return ref;
        //         }).catch(error => 
        //             {
        //                 console.log(error);
        //             });
        // }



    });


    exports.updateExistsUser = functions.firestore.document("Users/{userId}")
    .onCreate((snapshot, context) =>
    {
        const userAddedData = snapshot.data();
        const userAddedName = userAddedData.name;
        const userAddedID = userAddedData.id;
        var userRef = db.collection("Users");
        var query = userRef.where("name" , "=", userAddedName);

        query.get().then((querySnapshot) => 
        {
            if(querySnapshot.docs.length > 1)
            {
                var newUserDoc;

                if(querySnapshot.docs[0].ref.id ===snapshot.ref.id)
                {
                    newUserDoc = querySnapshot.docs[1];
                }else
                {
                    newUserDoc = querySnapshot.docs[0];
                }

                const newUserData = newUserDoc.data();

                const newAge = parseInt(newUserData.age) + parseInt(userAddedData.age);

                let newData =
                {
                    "name" : userAddedName,
                    "age" : newAge,
                    "id" : newUserData.id
                }
                
                snapshot.ref.update(newData).then(ref =>
                    {
                        newUserDoc.ref.delete();
                        return ref;
                    }).catch(updateError =>
                        {
                            console.log(updateError);
                        })


            }
            return querySnapshot;
        }).catch(error =>
            {
                console.log(error);
            })
    });