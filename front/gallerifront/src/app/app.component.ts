import { Component, OnInit, Input, ViewChild } from '@angular/core';
import { HttpService } from './http.service';
import { toImageList, Image } from 'src/image';

const LIMIT = 9; // 9 images per page

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html', styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'LocalGalleri';

  constructor(private http: HttpService) {
  }
}


