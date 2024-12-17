/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onRequest} = require("firebase-functions/v2/https");
const {onSchedule} = require("firebase-functions/v2/scheduler");
const admin = require("firebase-admin");
admin.initializeApp();

const logger = require("firebase-functions/logger");

// Scheduled function to reset streaks
exports.resetStreaks = onSchedule("every 24 hours", async (context) => {
  const firestore = admin.firestore();
  const realtimeDatabase = admin.database();

  const today = new Date().toISOString().split("T")[0];

  try {
    const usersSnapshot = await firestore.collection("users").get();
    usersSnapshot.forEach(async (userDoc) => {
      const userData = userDoc.data();
      const lastQuizAttemptDate = userData.lastQuizAttemptDate;

      if (lastQuizAttemptDate !== today) {
        // Reset streak in Firestore
        await userDoc.ref.update({
          streakCount: 0,
          lastQuizAttemptDate: today,
        });

        // Reset streak in Realtime Database
        const phoneNumber = userDoc.id;
        await realtimeDatabase.ref(`profiles/${phoneNumber}`).update({
          streakCount: 0,
          lastQuizAttemptDate: today,
        });
      }
    });

    logger.info("Streaks checked and updated if necessary.");
  } catch (error) {
    logger.error("Error resetting streaks:", error);
  }
});

// Example function to check if everything is working
exports.helloWorld = onRequest((request, response) => {
  logger.info("Hello logs!", {structuredData: true});
  response.send("Hello from Firebase!");
});
