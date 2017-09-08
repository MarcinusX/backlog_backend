# SwimHelper [![Build Status](https://travis-ci.org/moniaS/swimHelper.svg?branch=master)](https://travis-ci.org/moniaS/swimHelper) [![Coverage Status](https://coveralls.io/repos/github/moniaS/swimHelper/badge.svg?branch=master)](https://coveralls.io/github/moniaS/swimHelper?branch=master)

SwimHelper is an application which can be used by people who want to generate trainings on the basis of their requirements, manage their swimming statistics and notifications of trainings, join public competitions and track completed distance or burnt calories.

## Functionalities:

### Training generator
User has to choose styles in which he wants to train (backstroke, breaststroke, freestyle, butterfly), intensity level (low, medium, high) and also max duration of training (optionally max distance of training). Style statistics of user in specific styles, that he chose are also required. On the basis of these parameters trainings are generated from the pool of exercises. Every training contains warm up (at the beginning) and relax exercises (at the end).

### Calories calculator
User has a chance to calculate burned calories during the training/trainings. The number of calories is counted on the basis of weight, swimming style, speed and duration of exercise. The formula can be applied to one or more exercies. In second case we use Fork Join to optimize calculating process.

### Distance tracker
After completing the training there is a possibility to calculate distance of training/trainings. User can choose one training, range of dates or all his trainings to track the completed distance of them. The distance is calculated based on number of completed repeats and distance of specific exercise.

### Competition management
User can create and join public competitions. Competitions have defined maximum capacity and if that value is reached, no user can join given competition anymore. In order to prevent scenario when two users claim last spot at the same time, we introduced versioning and optimistic lock.

### Notifications
When user enters requirements for training, she or he can add a notification time, at which he would like to be reminded about upcoming training. Application checks every minute using scheduler for a training that needs to be reminded about and then send proper email to an user.

### Users and security
Every person can create its own account with unique email(username). After that, all requests require users to be authenticated with basic auth. User can freely update his personal data and statistics. There are two types of actors: user and admin. Only admin can make other users admins. First admin needs to be created manually.

## Worth-mentioning features

* <b>AOP</b> - We are using aspects for logging invokes of controller methods and measuring time of methods annotated with @TimeMeasured annotation
* <b>Security</b> - Application is secured with basic auth. Every method except registering requires authentication (it is defined in configuration class). Most of security requirements are mentioned in controller method annotations like @PreAuthorize or @RolesAllowed
* <b>Java Concurrency API</b> - In order to handle possible long execution of calories calculation, we introduced Fork Join mechanism to perform calculation for large number of exercise series separately. 
