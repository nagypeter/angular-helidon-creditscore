#
# Copyright (c) 2018, 2019 Oracle and/or its affiliates. All rights reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# 1st stage, build the app
FROM maven:3.6.1-jdk-8 as build

WORKDIR /angular-helidon-creditscore

# Create a first layer to cache the "Maven World" in the local repository.
# Incremental docker builds will always resume after that, unless you update
# the pom
ADD pom.xml .
RUN mvn package -DskipTests -P jar


# Do the Maven build!
# Incremental docker builds will resume here when you change sources
ADD src src
RUN mvn package -DskipTests -P jar
RUN pwd && ls -la target

RUN echo "Build is done!"

# 2nd stage, build the runtime image
FROM openjdk:8-jre-slim
WORKDIR /angular-helidon-creditscore

# Copy the binary built in the 1st stage
COPY --from=build /angular-helidon-creditscore/target/creditscore-se.jar ./
COPY --from=build /angular-helidon-creditscore/target/libs ./libs

RUN pwd && ls -la

CMD ["java", "-jar", "creditscore-se.jar"]

EXPOSE 8083
