# react-native-upi

<img src="./art/art.png" alt="My cool logo"/>

[react-native-upi](https://github.com/nitish24p/react-native-upi/tree/master/lib) is a tiny plugin to integrate the UPI payment interface made by [NPCI](https://www.bhimupi.org.in/) from your react native apps. This plugin allows you to enable peer to peer payments via UPI in your react native apps. Linking specs have been followed as per [this](https://www.npci.org.in/sites/all/themes/npcl/images/PDF/UPI_Linking_Specs_ver_1.5.1.pdf) doc


## Usage

```javascript
RNUpiPayment.initializePayment({
  vpa: 'john@upi', // or can be john@ybl or mobileNo@upi
  payeeName: 'John Doe',
  amount: '1',
  transactionRef: 'aasf-332-aoei-fn'
}, successCallback, failureCallback);

```

## Config docs
```javascript
{
  /*
  * REQUIRED
  * vpa is the address of the payee given to you
  * by your bank
  */
  vpa: 'somehandle@upi',

  /*
  * REQUIRED
  * payeeName is the name of the payee you want
  * to make a payment too. Some upi apps need this
  * hence it is a required field
  */
  payeeName: 'Payee name',

  /*
  * REQUIRED
  * This is a reference created by you / your server
  * which can help you identify this transaction
  * The UPI spec doesnt mandate this but its a good to have
  */
  transactionRef: 'some-hash-string',

  /*
  * REQUIRED
  * The actual amount to be transferred
  */
  amount: '200',

  /*
  * OPTIONAL
  * Transactional message to be shown in upi apps
  */
  transactionNote: 'for food'
}
```

## Callbacks 
```javascript
function successCallback(data) {
  // do whatever with the data
}

function failureCallback(data) {
  // do whatever with the data
}

```

## Responses
