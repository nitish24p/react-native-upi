import { NativeModules } from 'react-native';

const UpiModule = NativeModules.UpiPayment;

const RNUpiPayment = {
  requiredFields: ['vpa', 'amount', 'payeeName', 'transactionRef'],

  upiConfig: {
    vpa: 'pa',
    payeeName: 'pn',
    transactionRef: 'tr',
    amount: 'am',
    transactionNote: 'tn',
    currency: 'cu',
    merchantCode: 'mc',
  },

  defaultValues: {
    currency: 'INR',
    merchantCode: '',
  },

  UPI_APP_NOT_INSTALLED: 'UPI supporting app not installed',
  REQUEST_CODE_MISMATCH: 'Request Code Mismatch',
  NO_ACTION_TAKEN: 'No action taken',

  validateObject(config: Object) {
    const errorArray = [];
    this.requiredFields.forEach((eachField) => {
      if (!config[eachField]) {
        errorArray.push(eachField);
      }
    });

    return errorArray;
  },

  successCallback(success: Function) {
    return (data) => {
      data = JSON.parse(data);
      const successString = data.nameValuePairs && data.nameValuePairs.message;
      let successObj = this.convertStringToObject(successString);
      successObj.status = data.status;
      success(successObj);
    };
  },

  failureCallback(failure: Function) {
    return (data) => {
      data = JSON.parse(data);
      let failureObj = {};

      if (typeof data.nameValuePairs.message == 'undefined') {
        failure(data.nameValuePairs);
      } else {
        const failureString =
          data.nameValuePairs && data.nameValuePairs.message;
        if (
          failureString === this.UPI_APP_NOT_INSTALLED ||
          failureString === this.REQUEST_CODE_MISMATCH ||
          failureString === this.NO_ACTION_TAKEN
        ) {
          failure(data.nameValuePairs);
        } else {
          failureObj = this.convertStringToObject(failureString);
          failure(failureObj);
        }
      }
    };
  },

  convertStringToObject(responseString: string) {
    let object = {};
    const stringArray = responseString.split('&');
    object = stringArray.reduce((accumulator, current) => {
      const currentArray = current.split('=');
      accumulator[currentArray[0]] = currentArray[1];
      return accumulator;
    }, {});

    return object;
  },

  initializePayment(config, success, failure) {
    if (typeof success !== 'function') {
      throw new Error('Success callback not a function');
    }

    if (typeof failure !== 'function') {
      throw new Error('Failure callback not a function');
    }

    if (typeof config !== 'object') {
      throw new Error('config not of type object');
    }
    const errorArray = this.validateObject(config);

    if (errorArray.length > 0) {
      throw new Error(
        `Following keys are required ${JSON.stringify(errorArray)}`
      );
    }

    config.currency = 'INR';
    let upiString = 'upi://pay?';

    let queryString = Object.keys(config).reduce((accumulator, current) => {
      let prefix = '';
      if (accumulator) {
        prefix = '&';
      }
      accumulator =
        accumulator +
        prefix +
        `${this.upiConfig[current]}=${encodeURIComponent(
          config[current] || this.defaultValues[current] || ''
        )}`;

      return accumulator;
    }, '');
    const upiConfig = {};
    upiConfig.upiString = `upi://pay?${queryString}`;
    UpiModule.intializePayment(
      upiConfig,
      this.successCallback(success),
      this.failureCallback(failure)
    );
  },
};

module.exports = RNUpiPayment;
