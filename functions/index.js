const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.getTime = functions.https.onCall((data,context)=>{
return Date.now()
});

exports.updateNews = functions.database.ref('/news/{pushId}')
 .onWrite((snap,context) => {
	 const newValue = snap.after.val();
	 const previousValue = snap.before.val();
	 const timestamp =  -1 * Date.now();
 if(newValue.title !== previousValue.title){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedNewsNotification(snap , timestamp));
 }else if (newValue.textNews !== previousValue.textNews){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedNewsNotification(snap, timestamp));
 }else{
	 return console.log('time Updated');
 }
 });
 
exports.createNews = functions.database.ref('/news/{pushId}')
 .onCreate((snap,context) => {
	 const timestamp =  -1 * Date.now();
	 return snap.ref.child('timestamp').set(timestamp).then(sendNewNewsNotification(snap , timestamp));
 });
 
 function sendEditedNewsNotification(snap , timestamp) {
      const post = snap.after.val();
      // do stuff with post here
	  const title = post.title;
	   const textNews = post.textNews;
	   if (typeof title === 'undefined') {
		   return 0;
	   }else if (textNews === 'undefined'){
		   return console.log('undefined');
	   }else{
     const payload = {
		 data: {
         title:  'News : '+ post.title,
         body: post.textNews,
		fromEmail: post.fromEmail,
		timestamp: timestamp.toString()
		}
     };
/* Create an options object that contains the time to live for the notification and the priority. */
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 * 28//24 hours * 28
    };	 
		 
	    admin.messaging().sendToTopic("notifications_news", payload, options)
		return console.log('Finish');
	  }
}


 function sendNewNewsNotification(snap , timestamp) {
      const post = snap.val();
      // do stuff with post here
	  const title = post.title;
	   const textNews = post.textNews;
	   if (typeof title === 'undefined') {
		   return 0;
	   }else if (textNews === 'undefined'){
		   return console.log('undefined');
	   }else{
     const payload = {
		 data: {
         title:  'News : '+ post.title,
         body: post.textNews,
		fromEmail: post.fromEmail,
		timestamp: timestamp.toString()
		}
     };
/* Create an options object that contains the time to live for the notification and the priority. */
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 * 28//24 hours * 28
    };	 
		 
	    admin.messaging().sendToTopic("notifications_news", payload, options)
		return console.log('Finish');
	  }
}









exports.updateTask = functions.database.ref('/tasks/{pushId}')
 .onWrite((snap,context) => {
	 const newValue = snap.after.val();
	 const previousValue = snap.before.val();
	 const timestamp =  -1 * Date.now();
 if(newValue.title !== previousValue.title){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedTaskNotification(snap , timestamp));
 }
 if (newValue.area !== previousValue.area){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedTaskNotification(snap, timestamp));
 }
 if (newValue.details !== previousValue.details){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedTaskNotification(snap, timestamp));
 }
 if (newValue.fromDate !== previousValue.fromDate){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedTaskNotification(snap, timestamp));
 }
 if (newValue.toDate !== previousValue.toDate){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedTaskNotification(snap, timestamp));
 }if (newValue.toEmployee.email !== previousValue.toEmployee.email){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedTaskNotification(snap, timestamp));
 }
	 return console.log('time Updated');
 });
 
exports.createTask = functions.database.ref('/tasks/{pushId}')
 .onCreate((snap,context) => {
	 const timestamp =  -1 * Date.now();
	 return snap.ref.child('timestamp').set(timestamp).then(sendNewTaskNotification(snap , timestamp));
 });
 
 function sendEditedTaskNotification(snap , timestamp) {
      const post = snap.after.val();
      // do stuff with post here
	  const title = post.title;
	  const details = post.details;
	  const fromEmployee = post.fromEmployee.email;
	  const toEmployee = post.toEmployee.email;

	   if (typeof title === 'undefined') {
		   return 0;
	   }else if (details === 'undefined'){
		   return console.log('undefined');
	   }else{
     const payload = {
		 data: {
         title:  'Task : '+ post.title,
         body: post.details,
		fromEmployee: post.fromEmployee.email,
		toEmployee: post.toEmployee.email,
		timestamp: timestamp.toString()
		}
     };
/* Create an options object that contains the time to live for the notification and the priority. */
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 * 28//24 hours * 28
    };	 
		 
	    admin.messaging().sendToTopic("notifications_tasks", payload, options)
		return console.log('Task Notification Sent');
	  }
}


 function sendNewTaskNotification(snap , timestamp) {
      const post = snap.val();
     // do stuff with post here
	 const title = post.title;
	  const details = post.details;
	  const fromEmployee = post.fromEmployee.email;
	  const toEmployee = post.toEmployee.email;

	   if (typeof title === 'undefined') {
		   return 0;
	   }else if (details === 'undefined'){
		   return console.log('undefined');
	   }else{
     const payload = {
		 data: {
         title:  'Task : '+ post.title,
         body: post.details,
		fromEmployee: post.fromEmployee.email,
		toEmployee: post.toEmployee.email,
		timestamp: timestamp.toString()
		}
     };
/* Create an options object that contains the time to live for the notification and the priority. */
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 * 28//24 hours * 28
    };	 
		 
	    admin.messaging().sendToTopic("notifications_tasks", payload, options)
		return console.log('Task Notification Sent');
	  }
}



exports.updateEvent = functions.database.ref('/events/{pushId}')
 .onWrite((snap,context) => {
	 const newValue = snap.after.val();
	 const previousValue = snap.before.val();
	 const timestamp =  -1 * Date.now();
 if(newValue.title !== previousValue.title){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedEventNotification(snap , timestamp));
 }
 if (newValue.details !== previousValue.details){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedEventNotification(snap, timestamp));
 }
 if (newValue.date !== previousValue.date){
	 return snap.after.ref.child('timestamp').set(timestamp).then(sendEditedEventNotification(snap, timestamp));
 }
	 return console.log('time Updated');
 });
 
exports.createEvent = functions.database.ref('/events/{pushId}')
 .onCreate((snap,context) => {
	 const timestamp =  -1 * Date.now();
	 return snap.ref.child('timestamp').set(timestamp).then(sendNewEventNotification(snap , timestamp));
 });
 
 function sendEditedEventNotification(snap , timestamp) {
      const post = snap.after.val();
      // do stuff with post here
	  const title = post.title;
	  const details = post.details;
	  const fromEmployee = post.fromEmployee.email;
	  const toEmployee = post.toEmployee.email;

	   if (typeof title === 'undefined') {
		   return 0;
	   }else if (details === 'undefined'){
		   return console.log('undefined');
	   }else{
     const payload = {
		 data: {
         title:  'Event : '+ post.title,
         body: post.details,
		fromEmployee: post.fromEmployee.email,		
		timestamp: timestamp.toString()
		}
     };
/* Create an options object that contains the time to live for the notification and the priority. */
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 * 28//24 hours * 28
    };	 
		 
	    admin.messaging().sendToTopic("notifications_events", payload, options)
		return console.log('Event Notification Sent');
	  }
}


 function sendNewEventNotification(snap , timestamp) {
      const post = snap.val();
     // do stuff with post here
	 const title = post.title;
	  const details = post.details;
	  const fromEmployee = post.fromEmployee.email;
	  const toEmployee = post.toEmployee.email;

	   if (typeof title === 'undefined') {
		   return 0;
	   }else if (details === 'undefined'){
		   return console.log('undefined');
	   }else{
     const payload = {
		 data: {
         title:  'Event : '+ post.title,
         body: post.details,
		fromEmployee: post.fromEmployee.email,
		timestamp: timestamp.toString()
		}
     };
/* Create an options object that contains the time to live for the notification and the priority. */
    const options = {
        priority: "high",
        timeToLive: 60 * 60 * 24 * 28//24 hours * 28
    };	 
		 
	    admin.messaging().sendToTopic("notifications_events", payload, options)
		return console.log('Event Notification Sent');
	  }
}
