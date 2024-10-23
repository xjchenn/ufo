var app = angular.module('FacilitiesList', ['rzModule']);

app.controller('myCtrl', function ($scope, $http, $httpParamSerializerJQLike){

  $scope.dataLoaded = false;
  $scope.slider = {
    options: {
      floor: 0,
      ceil: 5
    }
  };

  $scope.facilities = [{name:'UWPlacebadminton', availability: 5}, {name:'UWPlacetable tennis', availability: 3}];

  $scope.AVAILABILITY = ['Closed','Long Wait','Full','Almost Full','Available','Empty'];
  
  $http({
    method: 'GET',
    url: '/myFacilities'
  }).then(function successCallback(response) {
    var facilitiesObj = angular.fromJson(response);
    $scope.facilities = facilitiesObj.data.facilityList;
    $scope.dataLoaded = true;
  });

  $scope.updateAvailability = function(facility) {
    requestData = {name: facility.name, availability: facility.availability};
    $http({
      method: 'POST',
      url: '/facility/setAvailability/',
      headers: {'Content-Type': 'application/x-www-form-urlencoded'},
      data: $httpParamSerializerJQLike(requestData)
    });
  }

});
