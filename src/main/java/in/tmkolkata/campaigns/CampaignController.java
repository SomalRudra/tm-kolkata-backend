package in.tmkolkata.campaigns;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/campaigns")
public class CampaignController {

  private final CampaignService campaignService;

  public CampaignController(CampaignService campaignService) {
    this.campaignService = campaignService;
  }

  @PostMapping("/broadcast")
  public BroadcastResponse broadcast(@Valid @RequestBody BroadcastRequest request) {
    return campaignService.broadcast(request);
  }
}
