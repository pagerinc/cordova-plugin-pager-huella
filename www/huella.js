var argscheck = require('cordova/argscheck'),
    exec = require('cordova/exec');


function Huella() {
}

Huella.prototype.isAvailable = function() {
    argscheck.checkArgs('fF', 'Huella.isAvailable', arguments);
    return new Promise((resolve, reject) => {
      exec(resolve, reject, "Huella", "isAvailable", []);
    });
};

Huella.prototype.showUnlock = function(successCallback, errorCallback) {
    argscheck.checkArgs('fF', 'Huella.showUnlock', arguments);
    return new Promise((resolve, reject) => {
      exec(resolve, reject, "Huella", "showUnlock", []);
    });
};

module.exports = new Huella();
