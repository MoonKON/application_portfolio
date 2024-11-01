
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Up : MonoBehaviour
{
    [SerializeField] private float moveDistance = 5f; // 可在操作台更改的移动距离
    [SerializeField] private float speed = 2f;        // 可在操作台更改的速度

    private Vector3 startPosition;
    private bool movingUp = true;

    void Start()
    {
        // 记录物体的初始位置
        startPosition = transform.position;
    }

    void Update()
    {
        // 根据方向判断物体是向上还是向下移动
        if (movingUp)
        {
            // 向上移动
            transform.position = Vector3.MoveTowards(transform.position, startPosition + new Vector3(0, moveDistance, 0), speed * Time.deltaTime);

            // 如果物体到达了目标距离，则改变方向为向下
            if (Vector3.Distance(transform.position, startPosition + new Vector3(0, moveDistance, 0)) < 0.1f)
            {
                movingUp = false;
            }
        }
        else
        {
            // 向下移动
            transform.position = Vector3.MoveTowards(transform.position, startPosition, speed * Time.deltaTime);

            // 如果物体回到初始位置，则改变方向为向上
            if (Vector3.Distance(transform.position, startPosition) < 0.1f)
            {
                movingUp = true;
            }
        }
    }
}

