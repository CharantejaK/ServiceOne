var app = angular.module("myApp");
app.factory('myService', function( $http) {
 var savedData = {}, promise;
 function set(data) {
   savedData = data;
 }
 function getViewServiceCall(clientName) {      
          promise = $http({
              method: 'GET',
              url: '/getmockdatalist',
              params: {clientName: clientName}
          });
      // Return the promise to the controller
      return promise;
 }

 function saveRequestDetails(requestObj) { 
        // $http returns a promise, which has a then function, which also returns a promise
        promise = $http({
            method: 'POST',
            url: '/savemockdata',
            data: {request: requestObj.request, response:requestObj.response, contenttype:requestObj.contentTtype, client:requestObj.clientName, description:requestObj.description,serviceName:requestObj.serviceName}
        });
      // Return the promise to the controller
      return promise;
 }


 
 function getData() {
    return savedData;
 }

 return {
  set: set,
  getData: getData,
  getViewServiceCall: getViewServiceCall,
  saveRequestDetails:saveRequestDetails
 }

});