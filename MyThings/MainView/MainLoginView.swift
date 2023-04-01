//
//  MainLoginView.swift
//  MyThings
//
//  Created by 고명주 on 2023/03/29.
//

import SwiftUI

struct MainLoginView: View {
  @State var email: String = ""
  @State var password: String = ""
  @State var isShowingAlert: Bool = false
  @State var isLogin: Bool = false
  @State private var action: Int? = 1
  var viewmodel = SignInViewModel()
  var body: some View {
    VStack(alignment: .center, spacing: 20) {
      Spacer()
      NavigationLink(destination: MainTabView(), isActive: $isLogin) {
        Text("")
      }
      Text("MyThing")
        .fontWeight(.bold)
        .font(.title)
        .fontDesign(.monospaced)
      Spacer()
      HStack {
        Text("Email")
          .font(.custom("", size: 16))
          .fontDesign(.monospaced)
          .frame(width: 80)
        TextField("@를 포함한 이메일을 입력해주세요.", text: $email)
          .font(.custom("", size: 12))
          .padding()
          .background(Color(uiColor: .secondarySystemBackground))
          .cornerRadius(10)
      }
      HStack {
        Text("Password")
          .frame(width: 80)
          .font(.custom("", size: 16))
          .fontDesign(.monospaced)
        TextField("영문/숫자/특수문자 8자-20자 비밀번호 입력", text: $password)
          .font(.custom("", size: 12))
          .padding()
          .background(Color(uiColor: .secondarySystemBackground))
          .cornerRadius(10)
      }
      Button(action: {
        viewmodel.goLogin(email: email, password: password) {
          self.isLogin = true
          self.isShowingAlert = false
          print("isl \(isLogin) isshow\(isShowingAlert)")
        } fail: {
          self.isLogin = false
          self.isShowingAlert = true
          action = 1
        }
      }) {
        Text("로그인")
      }
      .alert("이메일 or 비밀번호를 재확인해주세요!", isPresented: $isShowingAlert, actions: {
      })
      .fontDesign(.monospaced)
      .fontWeight(.bold)
      .foregroundColor(.white)
      .padding(20)
      .frame(minWidth: 350)
      .frame(height: 50)
      .background(Color(.systemBlue))
      .cornerRadius(10)
      
      Spacer()
      HStack {
        Text("비밀번호를 잊어버리셨나요?")
          .foregroundColor(.gray)
          .font(.caption)
        NavigationLink {
          PasswordView(viewModel: SignupViewModel(network: NetworkService(configuration: .default)))
        } label: {
          Text("비밀번호 찾기 >")
            .fontDesign(.monospaced)
            .fontWeight(.bold)
            .foregroundColor(.black)
            .font(.caption)
        }
      }
      HStack {
        Text("아직 MyThing 회원이 아니신가요?")
          .foregroundColor(.gray)
          .font(.caption)
        NavigationLink {
          SignupView(passwordViewModel: PasswordViewModel(network: NetworkService(configuration: .default)), signupViewModel: SignupViewModel(network: NetworkService(configuration: .default)))
        } label: {
          Text("회원가입 하기 >")
            .fontDesign(.monospaced)
            .fontWeight(.bold)
            .foregroundColor(.black)
            .font(.caption)
        }
      }
    }
    .padding(20)
    .navigationBarHidden(true)
  }
}

struct MainLoginView_Previews: PreviewProvider {
  static var previews: some View {
    MainLoginView()
  }
}
