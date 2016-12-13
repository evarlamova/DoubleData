var module = angular.module('checksum', ['ngResource', 'ngCookies']);

module.controller('HashSum', ['$scope', '$cookieStore', '$http', function ($scope, $cookieStore, $http) {
    $scope.init = function () {
        if (!$cookieStore.get('userUuid')) {
            $http.get('user',
                {data: {name: name}}).success(function (data) {
                $scope.user = data.uuid;
                $cookieStore.put('userUuid', data.uuid);
            });
        } else {
            $scope.user = $cookieStore.get('userUuid');
        }
    };

    $scope.initRadioList = function () {
        $http.get('taskTypes', {data: {name: name}}).success(function (data) {
            $scope.algoTypes = data;
        })
    }
}]);

module.directive('fileModel', ['$parse', function ($parse) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            var model = $parse(attrs.fileModel);
            var modelSetter = model.assign;

            element.bind('change', function () {
                scope.$apply(function () {
                    modelSetter(scope, element[0].files[0]);
                });
            });
        }
    };
}]);

module.service('fileUpload', ['$http',  function ($http) {
    this.uploadFileToUrl = function (file, linkInput, algoType, uuid, isLocal, uploadUrl) {
        var fd = new FormData();
        fd.append('file', file);
        fd.append('linkInput', linkInput);
        fd.append('algoType', algoType);
        fd.append('isLocal', isLocal);
        fd.append('userUuid', uuid);
        $http.post(uploadUrl, fd, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            })
            .success(function () {
            })
            .error(function () {
            });
    }
}]);

module.controller('Counting', ['$scope', '$cookieStore', 'fileUpload',
    function ($scope, $cookieStore, fileUpload) {
        $scope.uploadFile = function () {
            var file = $scope.fileInput;
            var linkInput = $scope.linkInput;
            var algoType = $scope.algoType;
            var isLocal = $scope.isLocal || false;
            var uploadUrl = '/api/fileupload';
            var uuid = $cookieStore.get('userUuid');
            fileUpload.uploadFileToUrl(file, linkInput, algoType, uuid, isLocal, uploadUrl);
        };
    }]);

module.controller('tasksStatus', ['$scope',  '$interval', '$http', '$cookieStore',
    function ($scope, $interval,  $http, $cookieStore) {

        setInterval(function () {
            $http.get($cookieStore.get('userUuid') + '/statusUpdate').success(function (data) {
                $scope.tasks = data;
            })
        }, 500);

        $scope.deleteTask = function (taskUuid) {
            $http.get($cookieStore.get('userUuid') + "/"
                + taskUuid +  '/deleteTask').success(function (data) {
            })
        }
    }
]);
