# ==============================================================================
# DNS Protector - Docker Build Environment
# Builds release and debug APKs from source in an isolated Ubuntu container
# ==============================================================================

FROM eclipse-temurin:17-jdk-jammy

# Set Environment Variables
ENV ANDROID_HOME="/opt/android-sdk"
ENV ANDROID_SDK_ROOT="/opt/android-sdk"
ENV PATH="${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools"
ENV DEBIAN_FRONTEND=noninteractive

# Install prerequisites
RUN apt-get update && apt-get install -y --no-install-recommends \
    wget \
    unzip \
    git \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Download and install Android Commandline Tools
WORKDIR /opt
RUN mkdir -p ${ANDROID_HOME}/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip -O cmdline-tools.zip && \
    unzip -q cmdline-tools.zip -d ${ANDROID_HOME}/cmdline-tools && \
    mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest && \
    rm cmdline-tools.zip

# Accept licenses and install Android SDK platform and build tools
RUN yes | sdkmanager --licenses && \
    sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Set working directory for app source code
WORKDIR /workspace

# Copy project files
COPY . /workspace

# Set executable permissions
RUN chmod +x ./gradlew 2>/dev/null || true

# Default command: build APK
CMD ["sh", "-c", "gradle assembleRelease --no-daemon || ./gradlew assembleRelease --no-daemon"]
