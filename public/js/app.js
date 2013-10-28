$('input#sign-up').click(function() {

  var email = $('input#email').val()

  if(!email) return;

  var success = function(a,b,c) {
    $('input#email').val("");
    $('div.error').html("");
    $('div.error').hide();
    $('div.thank-you').show();
    $('div.thank-you').html("Thanks! We'll let you know when we launch");
  };

  var error = function(a,b,c) {
    $('div.thank-you').html("");
    $('div.thank-you').hide();
    $('div.error').show();
    $('div.error').html("Please enter a valid email address.");
  };

  $.ajax({
    "method": "POST",
    "url"   : "/signup",
    "data"  : {
      "email": email
    },
    "success" : success,
    "error" : error
  });
});
