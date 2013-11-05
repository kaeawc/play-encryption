http://stackoverflow.com/questions/549/the-definitive-guide-to-form-based-website-authentication#477579

### PART I: How To Log In

* CAPTCHAs are not used

* Autocomplete is not turned off

* The only protection against login interception (packet sniffing) during login is done with SSL, which depends upon the existance of a cert or using a reverse proxy.

* The cookie has the secure and HTTP Only flags set.  The value of the cookie is not predictable because each it is encrypted with AES using the app's secret key which in a production environment should be an environment variable.  No other state is stored in the cookie.

### PART II: How To Remain Logged In - The Infamous "Remember Me" Checkbox

* The persistent login cookie (token) isn't stored, only a hash of it.

### PART III: Using Secret Questions

Are not used

### PART IV: Forgotten Password Functionality

TO DO

### PART V: Checking Password Strength

TO DO

### PART VI: Much More - Or: Preventing Rapid-Fire Login Attempts

TO DO

### PART VII: Distributed Brute Force Attacks

TO DO

### PART VIII: Two-Factor Authentication and Authentication Providers

TO DO

### MUST-READ LINKS About Web Authentication

[OWASP Guide To Authentication](http://www.owasp.org/index.php/Guide_to_Authentication)
[Dos and Don’ts of Client Authentication on the Web (very readable MIT research paper)](http://cookies.lcs.mit.edu/pubs/webauth%3atr.pdf)
[Charles Miller's Persistent Login Cookie Best Practice](http://fishbowl.pastiche.org/2004/01/19/persistent_login_cookie_best_practice/)
[Wikipedia: HTTP cookie](http://en.wikipedia.org/wiki/HTTP_cookie#Drawbacks_of_cookies)
[Personal knowledge questions for fallback authentication: Security questions in the era of Facebook (very readable Berkeley research paper)](http://cups.cs.cmu.edu/soups/2008/proceedings/p13Rabkin.pdf)
