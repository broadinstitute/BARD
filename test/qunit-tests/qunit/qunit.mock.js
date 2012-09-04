(function() {
  var Expectation, expectCall, finishMock, mock, mocked, mocking, stack, stub, testExpectations;
  var __bind = function(fn, me){ return function(){ return fn.apply(me, arguments); }; }, __slice = Array.prototype.slice;
  mocking = null;
  stack = [];
  Expectation = (function() {
    function Expectation(args) {
      $.each(args, __bind(function(i, el) {
        return this[i] = el;
      }, this));
      this.calledWith = [];
    }
    Expectation.prototype["with"] = function() {
      var args;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      return this.expectedArgs = args;
    };
    return Expectation;
  })();
  expectCall = function(object, method, calls) {
    var expectation;
    if (calls == null) {
      calls = 1;
    }
    expectation = new Expectation({
      object: object,
      method: method,
      expectedCalls: calls,
      originalMethod: object[method],
      callCount: 0
    });
    object[method] = function() {
      var args;
      args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
      expectation.originalMethod.apply(object, args);
      expectation.callCount += 1;
      return expectation.calledWith.push(args);
    };
    mocking.expectations.push(expectation);
    return expectation;
  };
  stub = function(object, method, fn) {
    var stb;
    stb = {
      object: object,
      method: method,
      original: object[method]
    };
    object[method] = fn;
    mocking.stubs.push(stb);
    return stb;
  };
  mock = function(test) {
    var mk;
    mk = {
      expectations: [],
      stubs: []
    };
    mocking = mk;
    stack.push(mk);
    test();
    if (!QUnit.config.blocking) {
      return finishMock();
    } else {
      return QUnit.config.queue.unshift(finishMock);
    }
  };
  mocked = function(fn) {
    return function() {
      return mock(fn);
    };
  };
  finishMock = function() {
    testExpectations();
    stack.pop();
    return mocking = stack.length > 0 ? stack[stack.length - 1] : null;
  };
  testExpectations = function() {
    var expectation, stb, _results;
    while (mocking.expectations.length > 0) {
      expectation = mocking.expectations.pop();
      equal(expectation.callCount, expectation.expectedCalls, "method " + expectation.method + " should be called " + expectation.expectedCalls + " times");
      if (expectation.expectedArgs) {
        $.each(expectation.calledWith, function(i, el) {
          return deepEqual(expectation.expectedArgs, el, "expected to be called with " + expectation.expectedArgs + ", called with " + el);
        });
      }
      expectation.object[expectation.method] = expectation.originalMethod;
    }
    _results = [];
    while (mocking.stubs.length > 0) {
      stb = mocking.stubs.pop();
      _results.push(stb.object[stb.method] = stb.original);
    }
    return _results;
  };
  window.expectCall = expectCall;
  window.stub = stub;
  window.mock = mock;
  window.QUnitMock = {
    mocking: mocking,
    stack: stack
  };
  window.test = function() {
    var arg, args, i, _len;
    args = 1 <= arguments.length ? __slice.call(arguments, 0) : [];
    for (i = 0, _len = args.length; i < _len; i++) {
      arg = args[i];
      if ($.isFunction(arg)) {
        args[i] = mocked(arg);
      }
    }
    return QUnit.test.apply(this, args);
  };
  window.asyncTest = function(testName, expected, callback) {
    if (arguments.length === 2) {
      callback = expected;
      expected = 0;
    }
    return QUnit.test(testName, expected, mocked(callback), true);
  };
}).call(this);
