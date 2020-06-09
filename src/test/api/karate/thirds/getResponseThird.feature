Feature: getResponseThird

  Background:
   #Getting token for admin and tso1-operator user calling getToken.feature
    * def signIn = call read('../common/getToken.feature') { username: 'admin'}
    * def authToken = signIn.authToken
    * def signInAsTSO = call read('../common/getToken.feature') { username: 'tso1-operator'}
    * def authTokenAsTSO = signInAsTSO.authToken

    * def thirdName = 'TEST_ACTION'
    * def process = 'process'
    * def state = 'response_full'
    * def version = 1

    Scenario: get third response

      Given url opfabUrl + '/thirds/' + thirdName + '/' + process + '/' + state + '/response?apiVersion=' + version
      And header Authorization = 'Bearer ' + authToken
      When method get
      Then print response
      And status 200
      And match response == {"btnText":{"parameters":null,"key":"action.text"},"btnColor":"RED","lock":true,"state":"responseState"}



  Scenario: get third response without authentication

    Given url opfabUrl + '/thirds/' + thirdName + '/' + process + '/' + state + '/response?apiVersion=' + version
    When method get
    Then print response
    And status 401


  Scenario: get third response without authentication

    Given url opfabUrl + '/thirds/unknownThird/' + process + '/' + state + '/response?apiVersion=' + version
    And header Authorization = 'Bearer ' + authToken
    When method get
    Then print response
    And status 404