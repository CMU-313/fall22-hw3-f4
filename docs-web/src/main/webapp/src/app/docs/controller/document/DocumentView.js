'use strict';

/**
 * Document view controller.
 */
angular.module('docs').controller('DocumentView', function ($scope, $rootScope, $state, $stateParams, $location, $dialog, $uibModal, Restangular, $translate) {
  // Load document data from server
  Restangular.one('document', $stateParams.id).get().then(function (data) {
    $scope.document = data;
  }, function (response) {
    $scope.error = response;
  });

  // Load comments from server
  Restangular.one('comment', $stateParams.id).get().then(function (data) {
    $scope.comments = data.comments;
  }, function (response) {
    $scope.commentsError = response;
  });

  /**
   * Add a skill score.
   */
  $scope.addSkillScore = function () {
    var score = parseInt($scope.skillScore);
    if (score >= 1 && score <= 5) {
      Restangular.one('score/skillScore').put({
        id: $stateParams.id,
        skillScore: $scope.skillScore
      }).then(function (data) {
        $scope.curscore = data.score;
      });
    } else {
      alert("Invalid Score! Score must be a numeric value between 1 and 5.")
    }
  };

  /**
   * Add a experience score.
   */
   $scope.addExperienceScore = function () {
    var score = parseInt($scope.experienceScore);
    if (score >= 1 && score <= 5) {
      Restangular.one('score/experienceScore').put({
        id: $stateParams.id,
        experienceScore: $scope.experienceScore
      }).then(function (data) {
        $scope.curscore = data.score;
      });
    } else {
      alert("Invalid Score! Score must be a numeric value between 1 and 5.")
    }
  };

  /**
   * Add an academic score.
   */
   $scope.addGPAScore = function () {
    var score = parseFloat($scope.GPAScore);
    if (score >= 0 && score <= 4) {
      Restangular.one('score/GPAScore').put({
        id: $stateParams.id,
        GPAScore: $scope.GPAScore
      }).then(function (data) {
        $scope.curscore = data.score;
      });
    } else {
      alert("Invalid Score! Score must be a numeric value between 0 and 4.")
    }
  };

  /**
   * Add a comment.
   */
  $scope.comment = '';
  $scope.addComment = function () {
    if ($scope.comment.length === 0) {
      return;
    }

    Restangular.one('comment').put({
      id: $stateParams.id,
      content: $scope.comment
    }).then(function (data) {
      $scope.comment = '';
      $scope.comments.push(data);
    });
  };

  /**
   * Delete a comment.
   */
  $scope.deleteComment = function (comment) {
    var title = $translate.instant('document.view.delete_comment_title');
    var msg = $translate.instant('document.view.delete_comment_message');
    var btns = [
      {result: 'cancel', label: $translate.instant('cancel')},
      {result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}
    ];

    $dialog.messageBox(title, msg, btns, function (result) {
      if (result === 'ok') {
        Restangular.one('comment', comment.id).remove().then(function () {
          $scope.comments.splice($scope.comments.indexOf(comment), 1);
        });
      }
    }); 
  };

  /**
   * Delete a document.
   */
  $scope.deleteDocument = function (document) {
    var title = $translate.instant('document.view.delete_document_title');
    var msg = $translate.instant('document.view.delete_document_message');
    var btns = [
      {result: 'cancel', label: $translate.instant('cancel')},
      {result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}
    ];

    $dialog.messageBox(title, msg, btns, function (result) {
      if (result === 'ok') {
        Restangular.one('document', document.id).remove().then(function () {
          $scope.loadDocuments();
          $state.go('document.default');
        });
      }
    });
  };

  /**
   * Open the share dialog.
   */
  $scope.share = function () {
    $uibModal.open({
      templateUrl: 'partial/docs/document.share.html',
      controller: 'DocumentModalShare'
    }).result.then(function (name) {
          if (name === null) {
            return;
          }

          // Share the document
          Restangular.one('share').put({
            name: name,
            id: $stateParams.id
          }).then(function (acl) {
                // Display the new share ACL and add it to the local ACLs
                $scope.showShare(acl);
                $scope.document.acls.push(acl);
                $scope.document.acls = angular.copy($scope.document.acls);
              })
        });
  };

  /**
   * Display a share.
   */
  $scope.showShare = function (share) {
    // Show the link
    var link = $location.absUrl().replace($location.path(), '').replace('#', '') + 'share.html#/share/' + $stateParams.id + '/' + share.id;
    var title = $translate.instant('document.view.shared_document_title');
    var msg = $translate.instant('document.view.shared_document_message', { link: link });
    var btns = [
      {result: 'close', label: $translate.instant('close')}
    ];

    if ($rootScope.userInfo.username !== 'guest') {
      btns.unshift({result: 'unshare', label: $translate.instant('unshare'), cssClass: 'btn-danger'});
    }

    $dialog.messageBox(title, msg, btns, function (result) {
      if (result === 'unshare') {
        // Unshare this document and update the local shares
        Restangular.one('share', share.id).remove().then(function () {
          $scope.document.acls = _.reject($scope.document.acls, function (s) {
            return share.id === s.id;
          });
        });
      }
    });
  };

  /**
   * Export the current document to PDF.
   */
  $scope.exportPdf = function () {
    $uibModal.open({
      templateUrl: 'partial/docs/document.pdf.html',
      controller: 'DocumentModalPdf'
    });

    return false;
  };

  /**
   * Validate the workflow.
   */
  $scope.validateWorkflow = function (transition) {
    Restangular.one('route').post('validate', {
      documentId: $stateParams.id,
      transition: transition,
      comment: $scope.workflowComment
    }).then(function (data) {
      $scope.workflowComment = '';
      var title = $translate.instant('document.view.workflow_validated_title');
      var msg = $translate.instant('document.view.workflow_validated_message');
      var btns = [{result: 'ok', label: $translate.instant('ok'), cssClass: 'btn-primary'}];
      $dialog.messageBox(title, msg, btns);

      if (data.readable) {
        $scope.document.route_step = data.route_step;
      } else {
        $state.go('document.default');
      }
    });
  };


  /**
   * Get number of grey stars
   */
   $scope.getGreyStars = function (num) {  
    var numberOfStars = Math.round(num);  
    var restStars = 5 - numberOfStars;  
    if (restStars > 0) {  
        var data = new Array(restStars);  
        for (var i = 0; i < data.length; i++) {  
            data[i] = i;  
        }  
        return data;  
    }  
  };

  /**
   * Get number of yellow stars
   */
  $scope.getYellowStars = function (num) {  
    var numberOfStars = Math.round(num);  
    if (numberOfStars > 5)  
        numberOfStars = 5;  
    var data = new Array(numberOfStars);  
    for (var i = 0; i < data.length; i++) {  
        data[i] = i;  
    }  
    return data;  
  };  
  
});