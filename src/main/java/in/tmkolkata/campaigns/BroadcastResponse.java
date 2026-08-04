package in.tmkolkata.campaigns;

import java.util.List;

public record BroadcastResponse(
    String channel,
    int requested_count,
    int sent_count,
    int failed_count,
    String status,
    String provider,
    String sender_email,
    List<String> errors,
    List<BroadcastRecipient> recipients
) {
}
