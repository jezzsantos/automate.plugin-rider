# Testing on Linux

1. Use docker to start a brand new Linux image: `docker pull Ubuntu`
2. Start the image in docker
3. Update apt: `apt-get update`
4. Install curl: `apt-get install curl`
5. Download installer from
   GitHub: `curl -o /usr/local/bin/automate https://github.com/jezzsantos/automate/releases/download/vx.x.x/automate.Linux-x64`
6. run: `chmod a+x automate`

6. Install these dependencies: `apt-get install libicu-dev`