import request from '@/utils/request'


export function addUser(data) {

  return request({

    url: '/user',

    method: 'post',

    data

  })

}

export function getUserList(params) {
  return request.get('/users', {
    params
  })

}
export function updateUser(data){

  return request({

    url:"/user",

    method:"put",

    data

  })

}
export function deleteUser(id) {
  return request.delete(`/user/${id}`)
}
