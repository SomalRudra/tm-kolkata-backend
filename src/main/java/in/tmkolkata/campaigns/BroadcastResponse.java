package in.tmkolkata.campaigns;

import java.util.List;

public record BroadcastResponse(
    String channel,
    int requested_count,
    int sent_count,
    int failed_count,
    String status,
    String provider,
    List<BroadcastRecipient> recipients
) {
}
