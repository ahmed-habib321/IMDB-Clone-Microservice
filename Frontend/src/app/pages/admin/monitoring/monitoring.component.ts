import { Component, inject } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-monitoring',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './monitoring.component.html',
})
export class MonitoringComponent {
  private readonly sanitizer = inject(DomSanitizer);

  readonly grafanaUrl = environment.grafanaUrl;
  readonly iframeUrl = `${environment.grafanaUrl}/?kiosk`;
  readonly safeIframeSrc: SafeResourceUrl = this.sanitizer.bypassSecurityTrustResourceUrl(
    `${environment.grafanaUrl}/?kiosk`,
  );

  openInNewTab(): void {
    window.open(this.iframeUrl, '_blank', 'noopener');
  }
}