/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */
// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });


const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

exports.sendMultiTopicNotification = functions.https.onCall(async (body, context) => {
    console.log('Received body:', body); // Add this line
    try {
        console.log('data:', body.data);
        const notification = body.data.notification;
        console.log('notification:', notification);
        /* data: {
            notification: {
                title: 'Bus Arrival Update',
                body: 'Bus Linha 5 (Solposto) arrived at R.de Ovar - Urbanização Santiago'
            },
            topic: 'L5AW2-Mon'
        }, 
  */
        topic = body.data.topic;
        console.log('topic:', topic);
        
        if (!topic) {
            throw new functions.https.HttpsError('invalid-argument', 'The function must be called with a topic.');
        }
        
        const message = {
            notification: notification,
            topic: topic
        };

        const response = await admin.messaging().send(message);
        
        console.log('Successfully sent messages:', response);
        return { success: true, result: response };
    } catch (error) {
        console.error('Error sending messages:', error);
        throw new functions.https.HttpsError('internal', 'Error sending notif', error);
    }
});