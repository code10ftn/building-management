import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AnnouncementListComponent } from '../announcements/announcement-list/announcement-list.component';
import { SigninComponent } from '../auth/signin/signin.component';
import { SignupComponent } from '../auth/signup/signup.component';
import { BuildingDetailComponent } from '../buildings/building-detail/building-detail.component';
import { BuildingListComponent } from '../buildings/building-list/building-list.component';
import { BuildingTenantListComponent } from '../buildings/building-tenant-list/building-tenant-list.component';
import { NewBuildingComponent } from '../buildings/new-building/new-building.component';
import { MalfunctionDetailComponent } from '../malfunctions/malfunction-detail/malfunction-detail.component';
import { MalfunctionListComponent } from '../malfunctions/malfunction-list/malfunction-list.component';
import { NewMalfunctionComponent } from '../malfunctions/new-malfunction/new-malfunction.component';
import { NewRepairmentComponent } from '../malfunctions/new-repairment/new-repairment.component';
import { MeetingDetailComponent } from '../meetings/meeting-detail/meeting-detail.component';
import { MeetingListComponent } from '../meetings/meeting-list/meeting-list.component';
import { NewMeetingComponent } from '../meetings/new-meeting/new-meeting.component';
import { ResidentialRequestsListComponent } from '../residential-requests/residential-requests-list/residential-requests-list.component';
import { SurveyAnsweringComponent } from '../surveys/survey-answering/survey-answering.component';
import { SurveyCreatingComponent } from '../surveys/survey-creating/survey-creating.component';
import { SurveyDetailsComponent } from '../surveys/survey-details/survey-details.component';
import { SurveyListComponent } from '../surveys/survey-list/survey-list.component';
import { HasAccessToBuildingGuard } from './has-access-to-building.guard';
import { HasAccessToMalfunctionsGuard } from './has-access-to-malfunctions.guard';
import { HasResidentialRequestsGuard } from './has-residential-requests.guard';
import { IsAdminGuard } from './is-admin.guard';
import { IsAuthenticatedGuard } from './is-authenticated.guard';
import { IsCompanyGuard } from './is-company.guard';
import { IsSupervisorOfBuildingGuard } from './is-supervisor-of-building.guard';
import { IsTenantOfBuildingGuard } from './is-tenant-of-building.guard';
import { IsUnuthenticatedGuard } from './is-unauthenticated.guard';

const routes: Routes = [
  { path: '', redirectTo: '/signin', pathMatch: 'full' },
  // Auth
  { path: 'signup', component: SignupComponent, canActivate: [IsUnuthenticatedGuard] },
  { path: 'signin', component: SigninComponent, canActivate: [IsUnuthenticatedGuard] },
  // Buildings
  { path: 'buildings', component: BuildingListComponent, canActivate: [IsAuthenticatedGuard] },
  { path: 'buildings/new', component: NewBuildingComponent, canActivate: [IsAdminGuard] },
  { path: 'buildings/:id', component: BuildingDetailComponent, canActivate: [IsAuthenticatedGuard] },
  // Announcements
  { path: 'buildings/:buildingId/announcements', component: AnnouncementListComponent, canActivate: [HasAccessToBuildingGuard] },
  // Meetings
  { path: 'buildings/:buildingId/meetings', component: MeetingListComponent, canActivate: [HasAccessToBuildingGuard] },
  { path: 'buildings/:buildingId/meetings/new', component: NewMeetingComponent, canActivate: [IsSupervisorOfBuildingGuard] },
  { path: 'buildings/:buildingId/meetings/:meetingId', component: MeetingDetailComponent, canActivate: [HasAccessToBuildingGuard] },
  // Malfunction
  { path: 'buildings/:buildingId/malfunctions', component: MalfunctionListComponent, canActivate: [HasAccessToMalfunctionsGuard] },
  { path: 'buildings/:buildingId/malfunctions/new', component: NewMalfunctionComponent, canActivate: [HasAccessToBuildingGuard] },
  { path: 'malfunctions', component: MalfunctionListComponent, canActivate: [IsCompanyGuard] },
  {
    path: 'buildings/:buildingId/malfunctions/:malfunctionId', component: MalfunctionDetailComponent,
    canActivate: [HasAccessToMalfunctionsGuard]
  },
  {
    path: 'buildings/:buildingId/malfunctions/:malfunctionId/repair',
    component: NewRepairmentComponent, canActivate: [HasAccessToMalfunctionsGuard]
  },
  // Surveys
  { path: 'buildings/:buildingId/surveys', component: SurveyListComponent, canActivate: [HasAccessToBuildingGuard] },
  { path: 'buildings/:buildingId/surveys/new', component: SurveyCreatingComponent, canActivate: [IsSupervisorOfBuildingGuard] },
  { path: 'buildings/:buildingId/surveys/:surveyId', component: SurveyDetailsComponent, canActivate: [HasAccessToBuildingGuard] },
  { path: 'buildings/:buildingId/surveys/:surveyId/answer', component: SurveyAnsweringComponent, canActivate: [IsTenantOfBuildingGuard] },
  // Tenants
  { path: 'buildings/:buildingId/tenants', component: BuildingTenantListComponent, canActivate: [IsAuthenticatedGuard] },
  // Residential requests
  { path: 'residentialRequests', component: ResidentialRequestsListComponent, canActivate: [HasResidentialRequestsGuard] }

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [
    IsAdminGuard,
    IsAuthenticatedGuard,
    IsCompanyGuard,
    IsSupervisorOfBuildingGuard,
    IsTenantOfBuildingGuard,
    IsUnuthenticatedGuard,
    HasAccessToBuildingGuard,
    HasAccessToMalfunctionsGuard,
    HasResidentialRequestsGuard
  ]
})
export class AppRoutingModule { }
