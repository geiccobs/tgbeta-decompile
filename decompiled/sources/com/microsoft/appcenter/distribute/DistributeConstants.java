package com.microsoft.appcenter.distribute;
/* loaded from: classes3.dex */
public final class DistributeConstants {
    static final String DEFAULT_API_URL = "https://api.appcenter.ms/v0.1";
    static final String DEFAULT_INSTALL_URL = "https://install.appcenter.ms";
    static final int DOWNLOAD_STATE_AVAILABLE = 1;
    static final int DOWNLOAD_STATE_COMPLETED = 0;
    static final int DOWNLOAD_STATE_ENQUEUED = 2;
    static final int DOWNLOAD_STATE_INSTALLING = 4;
    static final int DOWNLOAD_STATE_NOTIFIED = 3;
    static final String EXTRA_DISTRIBUTION_GROUP_ID = "distribution_group_id";
    static final String EXTRA_REQUEST_ID = "request_id";
    static final String EXTRA_TESTER_APP_UPDATE_SETUP_FAILED = "tester_app_update_setup_failed";
    static final String EXTRA_UPDATE_SETUP_FAILED = "update_setup_failed";
    static final String EXTRA_UPDATE_TOKEN = "update_token";
    static final String GET_LATEST_PRIVATE_RELEASE_PATH_FORMAT = "/sdk/apps/%s/releases/private/latest?release_hash=%s%s";
    static final String GET_LATEST_PUBLIC_RELEASE_PATH_FORMAT = "/public/sdk/apps/%s/releases/latest?release_hash=%s%s";
    public static final String HANDLER_TOKEN_CHECK_PROGRESS = "Distribute.handler_token_check_progress";
    static final String HEADER_API_TOKEN = "x-api-token";
    public static final long INVALID_DOWNLOAD_IDENTIFIER = -1;
    public static final long KIBIBYTE_IN_BYTES = 1024;
    public static final String LOG_TAG = "AppCenterDistribute";
    static final long MEBIBYTE_IN_BYTES = 1048576;
    static final String NOTIFICATION_CHANNEL_ID = "appcenter.distribute";
    static final String PARAMETER_DISTRIBUTION_GROUP_ID = "distribution_group_id";
    static final String PARAMETER_ENABLE_UPDATE_SETUP_FAILURE_REDIRECT_KEY = "enable_failure_redirect";
    static final String PARAMETER_INSTALL_ID = "install_id";
    static final String PARAMETER_PLATFORM = "platform";
    static final String PARAMETER_PLATFORM_VALUE = "Android";
    static final String PARAMETER_REDIRECT_ID = "redirect_id";
    static final String PARAMETER_REDIRECT_SCHEME = "redirect_scheme";
    static final String PARAMETER_RELEASE_HASH = "release_hash";
    static final String PARAMETER_RELEASE_ID = "downloaded_release_id";
    static final String PARAMETER_REQUEST_ID = "request_id";
    static final String PARAMETER_UPDATE_SETUP_FAILED = "update_setup_failed";
    static final long POSTPONE_TIME_THRESHOLD = 86400000;
    static final String PREFERENCE_KEY_DISTRIBUTION_GROUP_ID = "Distribute.distribution_group_id";
    static final String PREFERENCE_KEY_DOWNLOADED_DISTRIBUTION_GROUP_ID = "Distribute.downloaded_distribution_group_id";
    public static final String PREFERENCE_KEY_DOWNLOADED_RELEASE_FILE = "Distribute.downloaded_release_file";
    static final String PREFERENCE_KEY_DOWNLOADED_RELEASE_HASH = "Distribute.downloaded_release_hash";
    static final String PREFERENCE_KEY_DOWNLOADED_RELEASE_ID = "Distribute.downloaded_release_id";
    public static final String PREFERENCE_KEY_DOWNLOAD_ID = "Distribute.download_id";
    static final String PREFERENCE_KEY_DOWNLOAD_STATE = "Distribute.download_state";
    static final String PREFERENCE_KEY_DOWNLOAD_TIME = "Distribute.download_time";
    static final String PREFERENCE_KEY_POSTPONE_TIME = "Distribute.postpone_time";
    static final String PREFERENCE_KEY_RELEASE_DETAILS = "Distribute.release_details";
    static final String PREFERENCE_KEY_REQUEST_ID = "Distribute.request_id";
    static final String PREFERENCE_KEY_TESTER_APP_UPDATE_SETUP_FAILED_MESSAGE_KEY = "Distribute.tester_app_update_setup_failed_message";
    static final String PREFERENCE_KEY_UPDATE_SETUP_FAILED_MESSAGE_KEY = "Distribute.update_setup_failed_message";
    static final String PREFERENCE_KEY_UPDATE_SETUP_FAILED_PACKAGE_HASH_KEY = "Distribute.update_setup_failed_package_hash";
    static final String PREFERENCE_KEY_UPDATE_TOKEN = "Distribute.update_token";
    private static final String PREFERENCE_PREFIX = "Distribute.";
    static final String PRIVATE_UPDATE_SETUP_PATH_FORMAT = "/apps/%s/private-update-setup/";
    static final String SERVICE_NAME = "Distribute";
    public static final long UPDATE_PROGRESS_BYTES_THRESHOLD = 524288;
    public static final long UPDATE_PROGRESS_TIME_THRESHOLD = 500;

    DistributeConstants() {
    }
}
