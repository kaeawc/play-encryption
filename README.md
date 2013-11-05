http://stackoverflow.com/questions/549/the-definitive-guide-to-form-based-website-authentication#477579

### PART I: How To Log In

* CAPTCHAs are not used

* Autocomplete is not turned off

* The only protection against login interception (packet sniffing) during login is done with SSL, which depends upon the existance of a cert or using a reverse proxy.

* The cookie has the secure and HTTP Only flags set.  The value of the cookie is not predictable because each it is encrypted with AES using the app's secret key which in a production environment should be an environment variable.  No other state is stored in the cookie.

### PART II: How To Remain Logged In - The Infamous "Remember Me" Checkbox

Persistent Login Cookies ("remember me" functionality) are a danger zone; on the one hand, they are entirely as safe as conventional logins when users understand how to handle them; and on the other hand, they are an enormous security risk in the hands of most users, who use them on public computers, forget to log out, don't know what cookies are or how to delete them, etc.

Personally, I want my persistent logins for the web sites I visit on a regular basis, but I know how to handle them safely. If you are positive that your users know the same, you can use persistent logins with a clean conscience. If not - well, then you're more like me; subscribing to the philosophy that users who are careless with their login credentials brought it upon themselves if they get hacked. It's not like we go to our user's houses and tear off all those facepalm-inducing Post-It notes with passwords they have lined up on the edge of their monitors, either. If people are idiots, then let them eat idiot cake.

Of course, some systems can't afford to have any accounts hacked; for such systems, there is no way you can justify having persistent logins.

###### If you DO decide to implement persistent login cookies, this is how you do it:

* First, follow Charles Miller's 'Best Practices' article Do not get tempted to follow the 'Improved' Best Practices linked at the end of his article. Sadly, the 'improvements' to the scheme are easily thwarted (all an attacker has to do when stealing the 'improved' cookie is remember to delete the old one. This will require the legitimate user to re-login, creating a new series identifier and leaving the stolen one valid).

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
