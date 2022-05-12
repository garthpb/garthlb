# garthlb

To run:

- in CLI prompt,
- navigate to `garthlb` directory
- execute `mvn install` to build the service JAR
- execute `docker build -t garthlb .` to build service docker container
- execute `docker-compose up` to locally deploy redis service containers


To Test:

- `api-requests.json` contains Advanced Rest Client (ARC)-Compatible HTTP request lists, demonstrating basic functionality. 


DEVELOPMENT NOTES:

- I began by realizing I hadn't made a service, from absolute scratch, in a very long time.
- I researched options and decided on a mix of new and familiar:
    - I wanted to learn about Docker for deployment of the service and supported storage methods
    - I decided I'd implement the service in Java with Dropwizard because that was familiar to me
- For leaderboard storage, I considered using a mix of Redis (for performance) and mongoDB (for persistence).
    - I only ended up using redis for simplicity
    - MongoDB could be used for long-term persistence
- I ran into a couple issues/speed bumps
    - Dropwizard had less "out of the box" than I remembered; I hooked up what dependencies I needed to progress
    - The first redis client I tried didn't have an interface I liked, but I found "Redisson", which I liked better. It's "RScoredSortedSet" works great for my leaderboard implentation, with an interface I found clear.
    - I had a bit of trouble getting my service to work with a redis instance while both were running in Docker containers, but I figured it out. (Mostly it was because of lingering issues with my redis client migration - I hadn't cleaned up all of the old code, but I figured it out.)
- Regarding issues/speed bumps:
    - Normally when I have issues, if I can't solve it myself in a reasonably short time, I'd reach out to a teammate for suggestions. Since I mostly worked late at night, I didn't want to reach out on such significantly-off hours. Stepping away and coming back fresh helped me come up with new ideas that led to solutions, so I didn't have to. Still, in a normal work setting, I would reach out for assistance.
- I wanted to add more unit testing but I ran out of time.
- I wanted to add TestNG/"integ" testing, but that turned out to have quite a bit of overhead (hand-writing a service client, setting up the testng framework)
- Instead of TestNG/integ tests, I set up somewhat manual tests in Advanced Rest Client (ARC)
    - It isn't as robust as I'd like, but it demonstrates basic functionality.
- I realized I have leaderboardName as a path param for GET requests, but in body of POST requests - this was something I did as placeholder (as I forgot how path parameters mapped to object bodies in this framework), but I forgot to go back and fix it.
- For request, response, and general data objects, I generally like the builder pattern, but the serializers I'm using are incompatible with lombok's @Builder structure. Maybe there's a way around this but I didn't dig into it, for expediency.
- The "get scores for users" API is intended has a lot of user-specific lookups, which might not be too bad, but could potentially be improved by maintaining a fri
- I probably put in about 6 hours altogether during several short sittings.

 